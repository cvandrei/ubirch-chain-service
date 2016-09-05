package com.ubirch.chain.share.testutil

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.backend.chain.model.{FullBlock, GenesisBlock, HashRequest, HashedData}
import com.ubirch.chain.config.Config
import com.ubirch.chain.share.merkle.BlockUtil
import com.ubirch.chain.share.util.{HashRouteUtil, MiningUtil}
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.crypto.hash.HashUtil
import com.ubirch.util.date.DateUtil
import org.joda.time.DateTime
import org.scalatest.FeatureSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-08-30
  */
object BlockGenerator extends FeatureSpec
  with LazyLogging {

  private val miningUtil = new MiningUtil
  private val hashRouteUtil = new HashRouteUtil

  def createGenesisBlock(ageCheckResultsInTrue: Boolean = false): Future[GenesisBlock] = {

    val genesisTemplate = BlockUtil.genesisBlock()

    val genesisToPersist = ageCheckResultsInTrue match {

      case true =>
        val created = DateUtil.nowUTC.minusSeconds(Config.mineEveryXSeconds).minusSeconds(10)
        genesisTemplate.copy(created = created)

      case false => genesisTemplate

    }

    for {
      genesisCreated <- ChainStorageServiceClient.saveGenesisBlock(genesisToPersist)
    } yield {

      waitUntilGenesisBlockPersisted()
      genesisCreated.get

    }

  }

  def generateMinedBlock(elementCount: Int = 250): Future[FullBlock] = {

    HashGenerator.createXManyUnminedHashes(elementCount)
    miningUtil.mine() map {

      case None => fail("failed to generate a mined block")

      case Some(block) =>
        waitUntilBlockPersisted(block.hash)
        block

    }

  }

  def generateFullBlock(previousBlockHash: String,
                        previousBlockNumber: Long,
                        elementCount: Int = 250,
                        created: DateTime = DateUtil.nowUTC
                       ): Future[FullBlock] = {

    val hashes = HashUtil.randomSha256Hashes(elementCount)
    hashes map (HashRequest(_)) foreach hashRouteUtil.hash

    val currentBlockHash = BlockUtil.blockHash(hashes, previousBlockHash)
    val block = FullBlock(currentBlockHash, created, version = "1.0", previousBlockHash, previousBlockNumber + 1, Some(hashes))

    for {
      persisted <- ChainStorageServiceClient.upsertFullBlock(block)
    } yield {

      waitUntilBlockPersisted(currentBlockHash)
      persisted.get

    }


  }

  def waitUntilGenesisBlockPersisted(): Unit = {

    ChainStorageServiceClient.getGenesisBlock map {

      case None =>
        logger.info("genesis block has not been indexed yet...keep waiting")
        Thread.sleep(100)
        waitUntilGenesisBlockPersisted()

      case Some(block) => logger.info("genesis block has been indexed")

    }

  }

  def waitUntilBlockPersisted(hash: String): Unit = {

    ChainStorageServiceClient.getBlockInfo(HashedData(hash)) map {

      case None =>
        logger.info("block has not been indexed yet...keep waiting")
        Thread.sleep(100)
        waitUntilBlockPersisted(hash)

      case Some(block) => logger.info("block has been indexed")

    }

  }

}
