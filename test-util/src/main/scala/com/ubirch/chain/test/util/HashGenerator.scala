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
import scala.util.Random

/**
  * author: cvandrei
  * since: 2016-08-30
  */
object HashGenerator extends UnitSpec {

  def createUnminedHashes(sizeCheckResultsInTrue: Boolean): Long = {

    val blockMaxSize: Long = Config.blockMaxSizeKB
    val elemCount = sizeCheckResultsInTrue match {
      case true => (blockMaxSize / 32).toInt + 50
      case false => (blockMaxSize / 32).toInt
    }

    val randomHashes = HashUtil.randomSha256Hashes(elemCount)
    randomHashes map (HashedData(_)) foreach ChainStorageServiceClient.storeHash
    Thread.sleep(3000)

    val unminedHashes = Await.result(ChainStorageServiceClient.unminedHashes(), 3 seconds)

    val actualSize = BlockUtil.size(unminedHashes.hashes)
    sizeCheckResultsInTrue match {
      case true => actualSize should be > blockMaxSize
      case false => actualSize should be < blockMaxSize
    }

    actualSize

  }

  def createXManyUnminedHashes(count: Int): Long = {

    val randomHashes = HashUtil.randomSha256Hashes(count)
    randomHashes map (HashedData(_)) foreach ChainStorageServiceClient.storeHash
    Thread.sleep(1500)

    val unminedHashes = Await.result(ChainStorageServiceClient.unminedHashes(), 3 seconds)

    BlockUtil.size(unminedHashes.hashes)

  }

  /**
    * Convenience method generating n many random hashes (useful for tests).
    *
    * @param maxElementCount maximum number of randomly generated hashes
    * @return sequence of random hashes
    */
  def randomSha512Hashes(maxElementCount: Int = Random.nextInt(30000)): Seq[String] = {

    val randomSeq: Seq[String] = for (i <- 1 to maxElementCount) yield Random.nextLong.toString

    randomSeq.map(HashUtil.sha512HexString)

  }

}
