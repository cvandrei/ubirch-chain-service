package com.ubirch.chain.share.testutil

import com.ubirch.backend.chain.model.HashedData
import com.ubirch.chain.config.Config
import com.ubirch.chain.share.merkle.BlockUtil
import com.ubirch.chain.test.base.UnitSpec
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.crypto.hash.HashUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

/**
  * author: cvandrei
  * since: 2016-08-30
  */
object HashGenerator extends UnitSpec {

  /**
    * Creates enough random hashes to trigger mining based on the total size of all unmined hashes.
    *
    * Hashes are persisted and this method waits until we can query them from the storage. This means that once this
    * method has finished you can continue with your tests without delay.
    *
    * @param sizeCheckResultsInTrue true if size will trigger mining when checked
    * @return size of created hashes in byte
    */
  def createUnminedHashes(sizeCheckResultsInTrue: Boolean): Long = {

    val blockMaxSize: Long = Config.blockMaxSizeByte
    val elemCount = sizeCheckResultsInTrue match {
      // 64 is the number of bytes for a SHA-512 hash
      case true => (blockMaxSize / 64).toInt + 1
      case false => (blockMaxSize / 64).toInt
    }

    val size = createXManyUnminedHashes(elemCount)

    sizeCheckResultsInTrue match {
      case true => size should be > blockMaxSize
      case false => size should be < blockMaxSize
    }

    size

  }

  /**
    * Creates n random hashes, persists them and waits until we can query them from the storage. This means that once
    * this method has finished you can continue with your tests without delay.
    *
    * @param count how many hashes to create
    * @return size of created hashes in byte
    */
  def createXManyUnminedHashes(count: Int): Long = {

    /* by default ElasticSearch returns a maximum of 10000 elements for every search so we operate with SHA-512 hashes
     * instead of SHA-256 which is the server's default.
     */
    val randomHashes = randomSha512Hashes(count)
    val hashedData = randomHashes map (HashedData(_))
    hashedData foreach ChainStorageServiceClient.storeHash

    val size = BlockUtil.size(randomHashes)
    waitUntilHashesPersisted(size)

    size

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

  private def waitUntilHashesPersisted(expectedSize: Long): Unit = {

    ChainStorageServiceClient.unminedHashes.map { unmined =>
      isUnminedSizeReached(expectedSize, unmined.hashes) match {

        case true => // done

        case false =>
          Thread.sleep(100)
          waitUntilHashesPersisted(expectedSize)

      }
    }

  }

  private def isUnminedSizeReached(expectedSize: Long, hashes: Seq[String]) = BlockUtil.size(hashes) >= expectedSize

}
