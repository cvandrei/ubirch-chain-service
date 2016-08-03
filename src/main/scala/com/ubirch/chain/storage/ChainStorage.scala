package com.ubirch.chain.storage

import com.ubirch.chain.hash.HashUtil
import com.ubirch.chain.json.AnchorType._
import com.ubirch.chain.json.{Anchor, BlockInfo, UnminedHashes}

import scala.util.Random

/**
  * author: cvandrei
  * since: 2016-08-01
  */
trait ChainStorage {

  /**
    * Adds a hash to the list of unmined hashes.
    *
    * @param hash the hash to store
    */
  def storeHash(hash: String): Unit = ???

  /**
    * Gives us the block that the input hash is included in.
    *
    * @param hash hash based on which we look for the related block
    * @return block matching the input hash
    */
  def getHash(hash: String): Option[BlockInfo] = {

    // TODO implementation instead of the current dummy
    hash match {

      case "404" => None

      case _ =>

        val blockHash = HashUtil.hexString(s"block hash - $hash")
        val previousBlock = HashUtil.hexString(s"previous block hash - $hash")
        val bitcoinAnchorHash = HashUtil.hexString(s"bitcoin anchor - $hash")
        val ubirchAnchorHash = HashUtil.hexString(s"ubirch anchor - $hash")

        Some(
          BlockInfo(
            blockHash,
            previousBlockHash = previousBlock,
            anchors = Seq(
              Anchor(bitcoin, bitcoinAnchorHash),
              Anchor(ubirch, ubirchAnchorHash)
            )
          )
        )

    }

  }

  /**
    * Gives us basic information about a block (without all it's hashes).
    *
    * @param hash hash of the requested block
    * @return block matching the input hash
    */
  def getBlock(hash: String): Option[BlockInfo] = {

    // TODO implementation instead of the current dummy
    hash match {

      case "404" => None

      case _ =>

        val previousBlock = HashUtil.hexString(s"previous block hash - $hash")
        val bitcoinAnchorHash = HashUtil.hexString(s"bitcoin anchor - $hash")
        val ubirchAnchorHash = HashUtil.hexString(s"ubirch anchor - $hash")

        Some(
          BlockInfo(
            hash,
            previousBlockHash = previousBlock,
            anchors = Seq(
              Anchor(bitcoin, bitcoinAnchorHash),
              Anchor(ubirch, ubirchAnchorHash)
            )
          )
        )

    }

  }

  /**
    * Gives us a block including all it's hashes.
    *
    * @param hash hash of the requested block
    * @return block matching the input hash
    */
  def getBlockWithHashes(hash: String): Option[BlockInfo] = {

    // TODO implementation instead of the current dummy
    hash match {

      case "404" => None

      case _ =>

        val previousBlock = HashUtil.hexString(s"previous block hash - $hash")
        val bitcoinAnchorHash = HashUtil.hexString(s"bitcoin anchor - $hash")
        val ubirchAnchorHash = HashUtil.hexString(s"ubirch anchor - $hash")

        Some(
          BlockInfo(
            hash,
            previousBlockHash = previousBlock,
            anchors = Seq(
              Anchor(bitcoin, bitcoinAnchorHash),
              Anchor(ubirch, ubirchAnchorHash)
            ),
            hashes = Some(Seq("123", "456", "789"))
          )
        )

    }

  }

  /**
    * Gives us a list of hashes that haven't been mined yet.
    *
    * @return list of unmined hashes
    */
  def unminedHashes(): UnminedHashes = {

    // TODO implementation instead of the current dummy
    UnminedHashes(randomHashes)

  }

  /**
    * Saves a newly mined block.
    *
    * @param block block info to store
    */
  def saveMinedBlock(block: BlockInfo): Unit = ???

  private def randomHashes: Seq[String] = {

    val count = Random.nextInt(30000)
    val randomSeq: Seq[String] = for (i <- 1 to count) yield Random.nextLong.toString

    randomSeq.map(HashUtil.hexString)

  }

}