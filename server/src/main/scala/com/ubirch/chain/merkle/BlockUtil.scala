package com.ubirch.chain.merkle

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.json.{BlockInfo, GenesisBlock}
import com.ubirch.util.crypto.hash.HashUtil
import com.ubirch.util.crypto.hash.HashUtil._
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
  def blockHash(hashes: Seq[String], previousBlockHash: String): String = {

    val before = DateTime.now
    val treeHash = toMerkleTree(hashes).hash()
    val blockHash = sha256HexString(treeHash ++ previousBlockHash)
    val after = DateTime.now
    logger.debug(s"calculated block hash in ${after.getMillis - before.getMillis} ms")

    blockHash

  }

  /**
    * Creates a new BlockInfo for us.
    *
    * @param previousBlockHash hash of the previous block
    * @param hashes            hashes included in this block
    * @return new block object
    */
  def newBlock(previousBlockHash: String, hashes: Seq[String]): BlockInfo = {

    val hash = blockHash(hashes, previousBlockHash)
    BlockInfo(hash, previousBlockHash, hashes = Some(hashes))

  }

  def genesisBlock(): GenesisBlock = {

    val now = DateTime.now
    val hashes = HashUtil.randomSha256Hashes(500) :+ (now.getMillis + "ubirchChainService")
    val hash = BlockUtil.toMerkleTree(hashes).hash()

    GenesisBlock(hash, Some(hashes), now)

  }

}
