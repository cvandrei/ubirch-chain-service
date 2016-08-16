package com.ubirch.chain.core.server

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.core.config.{AppConst, Config}
import com.ubirch.chain.core.merkle.BlockUtil
import com.ubirch.chain.core.storage.ChainStorage
import com.ubirch.chain.json.BlockInfo

/**
  * author: cvandrei
  * since: 2016-08-16
  */
class MiningUtil extends ChainStorage
  with LazyLogging {

  def blockCheck(): Unit = {

    sizeCheck() match {

      case true => mine()

      case false =>

        ageCheck() match {
          case true => mine()
          case false => // do nothing
        }

    }

  }

  private def sizeCheck(): Boolean = {

    val blockMaxSizeKb = Config.blockMaxSize
    logger.debug(s"checking size of unmined hashes: ${AppConst.BLOCK_MAX_SIZE} = $blockMaxSizeKb kb")

    val hashes = unminedHashes()
    val size = BlockUtil.size(hashes)
    val sizeKb = BlockUtil.size(hashes) / 1000
    val maxBlockSizeBytes = Config.blockMaxSize * 1000

    size >= maxBlockSizeBytes match {

      case true =>
        logger.info(s"trigger mining of a new block (trigger: size) -- ${hashes.length} hashes ($sizeKb kb)")
        true

      case false => false

    }

  }

  private def ageCheck(): Boolean = {

    val block = mostRecentBlock()
    val nextCreationDate = block.created.plusSeconds(Config.mineEveryXSeconds)

    nextCreationDate.isBeforeNow match {

      case true =>
        logger.info("start mining a new block (trigger: time)")
        true

      case false =>
        logger.debug("don't have to mine a new block yet based on time trigger")
        false

    }

  }

  private def mine(): BlockInfo = {

    val previousBlockHash = mostRecentBlock().hash
    val hashes = unminedHashes()
    val block = BlockUtil.newBlock(previousBlockHash, hashes)
    val blockHash = block.hash
    logger.info(s"new block hash: $blockHash (blockSize=${BlockUtil.size(hashes) / 1000} kb; ${hashes.size} hashes)")

    upsertBlock(block)
    deleteHashes(hashes)

    block

  }

}
