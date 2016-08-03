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
          logger.info(s"start mining a new block (triggered by size check; blockSize: $sizeKb kb; ${hashes.size} hashes)")
          // TODO mine block

        case false => logger.debug(s"block does not need to be mined yet (size: $sizeKb kb; ${hashes.size} hashes)")

      }

    case m: Mine =>

      logger.info("start mining a new block (triggered by interval)")

      val hashes = unminedHashes().hashes
      val blockHash = BlockUtil.blockHash(hashes)
      logger.debug(s"new block hash: $blockHash (blockSize=${BlockUtil.size(hashes) / 1000} kb; ${hashes.size} hashes)")

      // TODO persist new block

      // TODO anchor (depending on config)

  }

}

case class SizeCheck()

case class Mine()
