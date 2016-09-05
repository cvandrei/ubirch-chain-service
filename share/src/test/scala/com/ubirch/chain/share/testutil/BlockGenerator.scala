package com.ubirch.chain.share.testutil

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.backend.chain.model.{FullBlock, GenesisBlock, HashRequest}
import com.ubirch.chain.config.Config
import com.ubirch.chain.share.merkle.BlockUtil
import com.ubirch.chain.share.util.{GenesisUtil, HashRouteUtil, MiningUtil}
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
  private val genesisUtil = new GenesisUtil

  private val defaultEventsInBlock: Int = 100

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

      genesisUtil.waitUntilGenesisIndexed()
      genesisCreated.get

    }

  }

  def generateMinedBlock(elementCount: Int = defaultEventsInBlock): Future[FullBlock] = {

    HashGenerator.createXManyUnminedHashes(elementCount)
    miningUtil.mine() map {

      case None => fail("failed to generate a mined block")

      case Some(block) =>
        miningUtil.waitUntilBlockIndexed(block.hash)
        block

    }

  }

  def generateFullBlock(previousBlockHash: String,
                        previousBlockNumber: Long,
                        elementCount: Int = defaultEventsInBlock,
                        created: DateTime = DateUtil.nowUTC
                       ): Future[FullBlock] = {

    val hashes = HashUtil.randomSha256Hashes(elementCount)
    hashes map (HashRequest(_)) foreach hashRouteUtil.hash

    val currentBlockHash = BlockUtil.blockHash(hashes, previousBlockHash)
    val block = FullBlock(currentBlockHash, created, version = "1.0", previousBlockHash, previousBlockNumber + 1, Some(hashes))

    for {
      persisted <- ChainStorageServiceClient.upsertFullBlock(block)
    } yield {

      miningUtil.waitUntilBlockIndexed(currentBlockHash)
      persisted.get

    }


  }

}
