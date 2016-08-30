package com.ubirch.chain.test.util

import com.ubirch.backend.chain.model.HashedData
import com.ubirch.chain.config.Config
import com.ubirch.chain.share.merkle.BlockUtil
import com.ubirch.chain.test.base.UnitSpec
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.crypto.hash.HashUtil

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2016-08-30
  */
object HashGenerator extends UnitSpec {

  def createUnminedHashes(belowSizeThreshold: Boolean): Long = {

    val blockMaxSize: Long = Config.blockMaxSize
    val elemCount = belowSizeThreshold match {
      case true => (blockMaxSize / 32).toInt
      case false => (blockMaxSize / 32).toInt + 1
    }

    val randomHashes = HashUtil.randomSha256Hashes(elemCount)
    belowSizeThreshold match {
      case true => BlockUtil.size(randomHashes) should be < blockMaxSize
      case false => BlockUtil.size(randomHashes) should be > blockMaxSize
    }
    randomHashes map (HashedData(_)) foreach ChainStorageServiceClient.storeHash
    Thread.sleep(3000)

    val unminedHashes = Await.result(ChainStorageServiceClient.unminedHashes(), 3 seconds)
    val size = BlockUtil.size(unminedHashes.hashes)
    belowSizeThreshold match {
      case true => size should be < blockMaxSize
      case false => size should be > blockMaxSize
    }

    size

  }

}
