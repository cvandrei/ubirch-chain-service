package com.ubirch.chain.backend.util

import com.jimjh.merkle.{Block, MerkleTree}
import com.roundeights.hasher.Hash

/**
  * author: cvandrei
  * since: 2016-07-29
  */
object BlockUtil {

  /**
    * @param hashes hashes to sum up for total size
    * @return total size in byte
    */
  def size(hashes: Seq[String]): Long = hashes.map(Hash(_).bytes.length.toLong).sum

  /**
    * Calculates a block hash.
    *
    * @param hashes hashes to calculate block hash from
    * @return hash as hex string
    */
  def blockHash(hashes: Seq[String]): String = {

    val blockSeq: Seq[Block] = hashes.map(_.getBytes.toSeq)
    val tree = MerkleTree(blockSeq, HashUtil.digest)
    tree.hashHex

  }

}
