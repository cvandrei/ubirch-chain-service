package com.ubirch.chain.share.testutil

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

  private val awaitTimeout = 10 seconds

  def createUnminedHashes(sizeCheckResultsInTrue: Boolean): Long = {

    val blockMaxSize: Long = Config.blockMaxSizeByte
    val elemCount = sizeCheckResultsInTrue match {
      // 64 is the number of bytes for a SHA-512 hash
      case true => (blockMaxSize / 64).toInt + 1
      case false => (blockMaxSize / 64).toInt
    }

    createXManyUnminedHashes(elemCount)
    Thread.sleep(2500)

    val unminedHashes = Await.result(ChainStorageServiceClient.unminedHashes(), awaitTimeout)

    val actualSize = BlockUtil.size(unminedHashes.hashes)
    sizeCheckResultsInTrue match {
      case true => actualSize should be > blockMaxSize
      case false => actualSize should be < blockMaxSize
    }

    actualSize

  }

  def createXManyUnminedHashes(count: Int): Long = {

    /* by default ElasticSearch returns a maximum of 10000 elements for every search so we operate with SHA-512 hashes
     * instead of SHA-256 which is the server's default.
     */
    val randomHashes = HashUtil.randomSha256Hashes(count)
    randomHashes map (HashedData(_)) foreach ChainStorageServiceClient.storeHash
    Thread.sleep(1500)

    val unminedHashes = Await.result(ChainStorageServiceClient.unminedHashes(), awaitTimeout)

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
