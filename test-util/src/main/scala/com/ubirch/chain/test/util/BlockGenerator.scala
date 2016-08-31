package com.ubirch.chain.test.util

import com.ubirch.backend.chain.model.{FullBlock, GenesisBlock, HashRequest, HashedData}
import com.ubirch.chain.config.Config
import com.ubirch.chain.share.merkle.BlockUtil
import com.ubirch.chain.share.util.{HashRouteUtil, MiningUtil}
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.crypto.hash.HashUtil
import org.joda.time.DateTime
import org.scalatest.FeatureSpec

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2016-08-22
  */
object BlockGenerator extends FeatureSpec {

  private val awaitTimeout = 100 seconds
  private val miningUtil = new MiningUtil
  private val hashRouteUtil = new HashRouteUtil

  def createGenesisBlock(ageCheckResultsInTrue: Boolean = false): GenesisBlock = {

    val genesisTemplate = BlockUtil.genesisBlock()

    val genesisToPersist = ageCheckResultsInTrue match {

      case true =>
        val created = DateTime.now.minusSeconds(Config.mineEveryXSeconds + 10)
        genesisTemplate.copy(created = created)

      case false => genesisTemplate

    }

    ChainStorageServiceClient.saveGenesisBlock(genesisToPersist)
    waitUntilGenesisBlockPersisted()

    genesisToPersist

  }

  def generateMinedBlock(elementCount: Int = 250): FullBlock = {

    HashGenerator.createXManyUnminedHashes(elementCount)

    val fullBlock = miningUtil.mine() map {

      case None => fail("failed to generate a mined block")

      case Some(block) =>
        waitUntilBlockPersisted(block.hash)
        block

    }

    Await.result(fullBlock, awaitTimeout)

  }

  def generateFullBlock(previousBlockHash: String,
                        previousBlockNumber: Int,
                        elementCount: Int = 250,
                        created: DateTime = DateTime.now
                       ): FullBlock = {

    val hashes = HashUtil.randomSha256Hashes(elementCount)
    hashes map (HashRequest(_)) foreach hashRouteUtil.hash

    val currentBlockHash = BlockUtil.blockHash(hashes, previousBlockHash)
    val fullBlock = FullBlock(currentBlockHash, created, version = "1.0", previousBlockHash, previousBlockNumber + 1, Some(hashes))

    ChainStorageServiceClient.upsertFullBlock(fullBlock)
    waitUntilBlockPersisted(currentBlockHash)

    fullBlock

  }

  private def waitUntilGenesisBlockPersisted(): Unit = {

    ChainStorageServiceClient.getGenesisBlock map {

      case None =>
        Thread.sleep(100)
        waitUntilGenesisBlockPersisted()

      case Some(block) => // done

    }

  }

  private def waitUntilBlockPersisted(hash: String): Unit = {

    ChainStorageServiceClient.getBlockInfo(HashedData(hash)) map {

      case None =>
        Thread.sleep(100)
        waitUntilBlockPersisted(hash)

      case Some(block) => // done

    }

  }

}
