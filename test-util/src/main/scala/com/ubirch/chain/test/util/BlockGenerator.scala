package com.ubirch.chain.test.util

import com.ubirch.backend.chain.model.{FullBlock, GenesisBlock, HashRequest}
import com.ubirch.chain.share.merkle.BlockUtil
import com.ubirch.chain.share.util.{HashRouteUtil, MiningUtil}
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.crypto.hash.HashUtil

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2016-08-22
  */
object BlockGenerator {

  private val miningUtil = new MiningUtil

  def createGenesisBlock(): GenesisBlock = {
    val genesis = BlockUtil.genesisBlock()
    Await.result(ChainStorageServiceClient.saveGenesisBlock(genesis), 2 seconds).get
  }

  def generateMinedBlock(elementCount: Int = 5000): FullBlock = {

    val hashes = HashUtil.randomSha256Hashes(elementCount) map (HashRequest(_))
    hashes foreach (new HashRouteUtil).hash
    Thread.sleep(1000)

    Await.result(miningUtil.mine(), 5 seconds).get

  }

}
