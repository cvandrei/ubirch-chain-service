package com.ubirch.chain.backend.util

import com.ubirch.chain.backend.merkle.Branch
import com.ubirch.chain.backend.util.HashUtil._

/**
  * author: cvandrei
  * since: 2016-07-29
  */
object BlockUtil {

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
  def toMerkleTree(hashes: Seq[String]): Branch = Branch.createFromHashes(hashes)

  /**
    * Calculates a block hash based on a list of hashes.
    *
    * @param hashes hashes to calculate block hash from
    * @return hash as hex string
    */
  def blockHash(hashes: Seq[String]): String = toMerkleTree(hashes).hash()

}
