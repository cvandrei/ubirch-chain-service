package com.ubirch.chain.share.testutil

import com.ubirch.backend.chain.model.{FullBlock, GenesisBlock, HashRequest}
import com.ubirch.chain.share.merkle.BlockUtil
import com.ubirch.chain.share.util.{HashRouteUtil, MiningUtil}
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.crypto.hash.HashUtil
import org.joda.time.DateTime
import org.scalatest.FeatureSpec

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2016-08-30
  */
object BlockGenerator extends FeatureSpec {

  val miningUtil = new MiningUtil
  val hashRouteUtil = new HashRouteUtil

  def createGenesisBlock(createdOpt: Option[DateTime] = None): GenesisBlock = {

    val genesis = BlockUtil.genesisBlock()

    val genesisToPersist = createdOpt match {
      case None => genesis
      case Some(created) => genesis.copy(created = created)
    }

    Await.result(ChainStorageServiceClient.saveGenesisBlock(genesisToPersist), 2 seconds).get

  }

  def generateMinedBlock(elementCount: Int = 1000): FullBlock = {

    val hashes = HashUtil.randomSha256Hashes(elementCount) map (HashRequest(_))
    hashes foreach hashRouteUtil.hash

    Await.result(miningUtil.mine(), 5 seconds).get

  }

  def generateFullBlock(previousBlockHash: String,
                        previousBlockNumber: Long,
                        elementCount: Int = 1000,
                        created: DateTime = DateTime.now
                       ): FullBlock = {

    val hashes = HashUtil.randomSha256Hashes(elementCount)
    hashes map (HashRequest(_)) foreach hashRouteUtil.hash

    val currentBlockHash = BlockUtil.blockHash(hashes, previousBlockHash)
    val fullBlock = FullBlock(currentBlockHash, created, version = "1.0", previousBlockHash, previousBlockNumber + 1, Some(hashes))

    Await.result(ChainStorageServiceClient.upsertFullBlock(fullBlock), 2 seconds).get

  }

}
