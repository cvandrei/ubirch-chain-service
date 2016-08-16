package com.ubirch.chain.storage

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.json.AnchorType._
import com.ubirch.chain.json.{HashInfo, Anchor, BlockInfo, GenesisBlock, Hash}
import com.ubirch.chain.merkle.BlockUtil
import com.ubirch.util.crypto.hash.HashUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
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
  def storeHash(hash: Hash): Unit = {
    // TODO implementation instead of the current dummy
    logger.info(s"storing hash: $hash")
  }

  /**
    * deletes a set of hash from the list of unmined hashes.
    *
    * @param hash set of hashes to delete
    */
  def deleteHashes(hash: Seq[String]): Future[Boolean] = {
    // TODO implementation instead of the current dummy
    Future(true)
  }

  /**
    * Gives us the block that the input hash is included in.
    *
    * @param hash hash based on which we look for the related block
    * @return block matching the input hash
    */
  def getHashInfo(hash: Hash): Option[HashInfo] = {

    // TODO implementation instead of the current dummy
    hash.hash match {

      case "404" => None

      case _ =>

        val blockHash = HashUtil.sha256HexString(s"block hash - $hash")

        Some(HashInfo(hash.hash, Some(blockHash)))

    }

  }

  /**
    * Gives us basic information about a block (without all it's hashes).
    *
    * @param hash hash of the requested block
    * @return block matching the input hash
    */
  def getBlock(hash: Hash): Option[BlockInfo] = {

    // TODO implementation instead of the current dummy
    hash.hash match {

      case "404" => None

      case _ =>

        val previousBlock = HashUtil.sha256HexString(s"previous block hash - $hash")
        val bitcoinAnchorHash = HashUtil.sha256HexString(s"bitcoin anchor - $hash")
        val ubirchAnchorHash = HashUtil.sha256HexString(s"ubirch anchor - $hash")

        Some(
          BlockInfo(
            hash.hash,
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
  def getBlockWithHashes(hash: Hash): Option[BlockInfo] = {

    // TODO implementation instead of the current dummy
    hash.hash match {

      case "404" => None

      case _ =>

        val previousBlock = HashUtil.sha256HexString(s"previous block hash - $hash")
        val bitcoinAnchorHash = HashUtil.sha256HexString(s"bitcoin anchor - $hash")
        val ubirchAnchorHash = HashUtil.sha256HexString(s"ubirch anchor - $hash")

        Some(
          BlockInfo(
            hash.hash,
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
  def unminedHashes(): Seq[String] = {

    // TODO implementation instead of the current dummy
    HashUtil.randomSha256Hashes()

  }

  /**
    * Saves or updates a block.
    *
    * @param block block info to store
    */
  def upsertBlock(block: BlockInfo): Unit = {

    // TODO implementation instead of the current dummy
    logger.info(s"upsert block: blockHash=${block.hash}, previousBlockHash=${block.previousBlockHash}")

  }

  /**
    * @return the most recent block
    */
  def mostRecentBlock(): BlockInfo = {

    // TODO implementation instead of the current dummy
    val hash = HashUtil.sha256HexString(Random.nextInt().toString)
    val previousBlock = HashUtil.sha256HexString(s"most recent's previous block hash - $hash")
    val bitcoinAnchorHash = HashUtil.sha256HexString(s"bitcoin anchor - $hash")
    val ubirchAnchorHash = HashUtil.sha256HexString(s"ubirch anchor - $hash")

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