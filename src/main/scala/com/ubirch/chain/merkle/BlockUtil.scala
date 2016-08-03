package com.ubirch.chain.merkle

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.hash.HashUtil._
import org.joda.time.DateTime

/**
  * author: cvandrei
  * since: 2016-07-29
  */
object BlockUtil extends LazyLogging {

  /**
    * @param hashes hashes to sum up for total size
    * @return total size in byte
    */
  def size(hashes: Seq[String]): Long = hashes.map(hashAsBytes(_).length.toLong).sum

  /**
    * Converts a list of strings into a Merkle Tree (represented by a [Branch] instance).
    *
    * @param hashes list of strings to convert to Merkle Tree
    * @return Merkle Tree created based on input
    */
  def toMerkleTree(hashes: Seq[String]): Branch = {

    val before = DateTime.now
    val tree = Branch.createFromHashes(hashes)
    val after = DateTime.now
    logger.debug(s"created merkle tree in ${after.getMillis - before.getMillis} ms")

    tree

  }

  /**
    * Calculates a block hash based on a list of hashes.
    *
    * @param hashes hashes to calculate block hash from
    * @return hash as hex string
    */
  def blockHash(hashes: Seq[String]): String = {

    val before = DateTime.now
    val blockHash = toMerkleTree(hashes).hash()
    val after = DateTime.now
    logger.debug(s"calculated block hash in ${after.getMillis - before.getMillis} ms")

    blockHash

  }

}
