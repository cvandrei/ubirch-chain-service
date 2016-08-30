package com.ubirch.chain.share.testutil

import com.ubirch.backend.chain.model.{FullBlock, GenesisBlock, HashRequest}
import com.ubirch.chain.share.merkle.BlockUtil
import com.ubirch.chain.share.util.{HashRouteUtil, MiningUtil}
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.crypto.hash.HashUtil
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
  val hashRoute = new HashRouteUtil

  def createGenesisBlock(): GenesisBlock = {
    val genesis = BlockUtil.genesisBlock()
    Await.result(ChainStorageServiceClient.saveGenesisBlock(genesis), 2 seconds).get
  }

  def generateMinedBlock(elementCount: Int = 1000): FullBlock = {

    val hashes = HashUtil.randomSha256Hashes(elementCount) map (HashRequest(_))
    hashes foreach hashRoute.hash

    Await.result(miningUtil.mine(), 5 seconds).get

  }

}
