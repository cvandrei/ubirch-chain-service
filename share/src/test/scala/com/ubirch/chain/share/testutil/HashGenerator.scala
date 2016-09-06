package com.ubirch.chain.share.testutil

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.backend.chain.model.HashedData
import com.ubirch.chain.config.Config
import com.ubirch.chain.share.merkle.BlockUtil
import com.ubirch.chain.test.base.UnitSpec
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.crypto.hash.HashUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.Random

/**
  * author: cvandrei
  * since: 2016-08-30
  */
object HashGenerator extends UnitSpec
  with LazyLogging {

  /** defines which hash algorithm to use when creating event hashes */
  private val hashAlgorithm = "SHA-512"

  private val timeout = 30 seconds

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
    val elemCount = calculateElementCount(sizeCheckResultsInTrue, hashAlgorithm)

    val size = createXManyUnminedHashes(elemCount)

    sizeCheckResultsInTrue match {
      case true => size should be > blockMaxSize
      case false => size should be < blockMaxSize
    }

    size

  }

  /**
    * Creates enough random hashes to trigger mining based on the total size of all unmined hashes.
    *
    * Hashes are persisted and this method waits until we can query them from the storage. This means that once this
    * method has finished you can continue with your tests without delay.
    *
    * @param sizeCheckResultsInTrue true if size will trigger mining when checked
    * @return size of created hashes in byte
    */
  def createUnminedHashesFuture(sizeCheckResultsInTrue: Boolean): Future[Long] = Future(createUnminedHashes(sizeCheckResultsInTrue))

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
    val randomHashes: Seq[String] = hashAlgorithm.toUpperCase match {
      case "SHA-256" => HashUtil.randomSha256Hashes(count)
      case "SHA-512" => randomSha512Hashes(count)
    }

    val hashedData = randomHashes map (HashedData(_))
    hashedData foreach ChainStorageServiceClient.storeHash

    val size = BlockUtil.size(randomHashes)
    waitUntilHashesPersisted(size)

    size

  }

  /**
    * Creates n random hashes, persists them and waits until we can query them from the storage. This means that once
    * this method has finished you can continue with your tests without delay.
    *
    * @param count how many hashes to create
    * @return size of created hashes in byte
    */
  def createXManyUnminedHashesFuture(count: Int): Future[Long] = Future(createXManyUnminedHashes(count))

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

    var unmined = Await.result(ChainStorageServiceClient.unminedHashes, timeout)
    while (!isUnminedSizeReached(expectedSize, unmined.hashes)) {

      logger.info("not all unmined hashes have been indexed yet...keep waiting")
      Thread.sleep(100)
      unmined = Await.result(ChainStorageServiceClient.unminedHashes, timeout)

    }

    logger.info("all unmined hashes have been indexed yet...done")

  }

  private def isUnminedSizeReached(expectedSize: Long, hashes: Seq[String]) = BlockUtil.size(hashes) >= expectedSize

  private def calculateElementCount(sizeCheckResultsInTrue: Boolean, algorithm: String): Int = {

    val hashSize = algorithm.toUpperCase match {
      case "SHA-256" => 32
      case "SHA-512" => 64
    }

    val blockMaxSize: Long = Config.blockMaxSizeByte

    sizeCheckResultsInTrue match {
      case true => (blockMaxSize / hashSize).toInt + 1
      case false => (blockMaxSize / hashSize).toInt
    }

  }

}
