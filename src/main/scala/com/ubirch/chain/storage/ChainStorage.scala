package com.ubirch.chain.storage

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.hash.HashUtil
import com.ubirch.chain.json.AnchorType._
import com.ubirch.chain.json.{Anchor, BlockInfo, GenesisBlock, UnminedHashes}
import com.ubirch.chain.merkle.BlockUtil

import scala.util.Random

/**
  * author: cvandrei
  * since: 2016-08-01
  */
trait ChainStorage extends LazyLogging {

  /**
    * Adds a hash to the list of unmined hashes.
    *
    * @param hash the hash to store
    */
  def storeHash(hash: String): Unit = {
    // TODO implementation instead of the current dummy
    logger.info(s"storing hash: $hash")
  }

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
    UnminedHashes(HashUtil.randomHashes())

  }

  /**
    * Saves or updates a block.
    *
    * @param block block info to store
    */
  def upsertBlock(block: BlockInfo): Unit = {

    // TODO implementation instead of the current dummy
    logger.info(s"upsert block: blockHash=${block.hash}, previousBlockHash=${block.previousBlockHash}")
    logger.debug(s"upsertblock: $block")

  }

  /**
    * @return the most recent block
    */
  def mostRecentBlock(): BlockInfo = {

    // TODO implementation instead of the current dummy
    val hash = HashUtil.hexString(Random.nextInt().toString)
    val previousBlock = HashUtil.hexString(s"most recent's previous block hash - $hash")
    val bitcoinAnchorHash = HashUtil.hexString(s"bitcoin anchor - $hash")
    val ubirchAnchorHash = HashUtil.hexString(s"ubirch anchor - $hash")

    BlockInfo(
      hash,
      previousBlockHash = previousBlock,
      anchors = Seq(
        Anchor(bitcoin, bitcoinAnchorHash),
        Anchor(ubirch, ubirchAnchorHash)
      )
    )

  }

  /**
    * Stores the genesis block unless we already have one.
    *
    * @param genesis the new genesis block
    */
  def saveGenesisBlock(genesis: GenesisBlock): Unit = {
    // TODO implementation instead of the current dummy
    logger.info(s"saved genesis block: hash=${genesis.hash}")
  }

  /**
    * @return the genesis block; None if none exists
    */
  def getGenesisBlock: Option[GenesisBlock] = {

    // TODO implementation instead of the current dummy
    Random.nextBoolean() match {
      case true => Some(BlockUtil.genesisBlock())
      case false => None
    }

  }

}