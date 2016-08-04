package com.ubirch.chain.backend.actor

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.backend.config.{AppConst, Config}
import com.ubirch.chain.merkle.BlockUtil
import com.ubirch.chain.storage.ChainStorage

/**
  * author: cvandrei
  * since: 2016-08-03
  */
class MiningActor extends Actor with ChainStorage with LazyLogging {

  override def receive: Receive = {

    case sc: SizeCheck =>

      val blockMaxSizeKb = Config.blockMaxSize
      logger.debug(s"checking size of unmined hashes: ${AppConst.BLOCK_MAX_SIZE} = $blockMaxSizeKb kb")

      val hashes = unminedHashes().hashes
      val size = BlockUtil.size(hashes)
      val sizeKb = size / 1000
      val maxBlockSizeBytes = Config.blockMaxSize * 1000

      size >= maxBlockSizeBytes match {

        case true =>
          logger.info(s"trigger mining of a new block (triggered by size check; blockSize: $sizeKb kb; ${hashes.size} hashes)")
          self ! Mine // TODO trigger does not seem to work --> FIX ME !!!

        case false => logger.debug(s"block does not need to be mined yet (size: $sizeKb kb; ${hashes.size} hashes)")

      }

    case m: Mine =>

      logger.info("start mining a new block")

      val previousBlockHash = mostRecentBlock.hash
      val hashes = unminedHashes().hashes
      val block = BlockUtil.newBlock(previousBlockHash, hashes)
      logger.debug(s"new block hash: ${block.hash} (blockSize=${BlockUtil.size(hashes) / 1000} kb; ${hashes.size} hashes)")

      saveMinedBlock(block)

      // TODO anchor (depending on config)

  }

}

case class SizeCheck()

case class Mine()
