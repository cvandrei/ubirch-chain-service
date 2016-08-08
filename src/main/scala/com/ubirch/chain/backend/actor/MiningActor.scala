package com.ubirch.chain.backend.actor

import akka.actor.Actor
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.backend.config.{AppConst, Config}
import com.ubirch.chain.json.BlockInfo
import com.ubirch.chain.merkle.BlockUtil
import com.ubirch.chain.storage.ChainStorage

/**
  * author: cvandrei
  * since: 2016-08-03
  */
class MiningActor extends Actor with ChainStorage with LazyLogging {

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {

    case sc: SizeCheck => sizeCheck()

    case m: BlockInterval =>

      // TODO check mostRecentBlock().created for time based trigger --> reduce SizeCheck and Mine to just one case class
      logger.info("start mining a new block")
      mine()

  }

  private def sizeCheck(): Unit = {

    val blockMaxSizeKb = Config.blockMaxSize
    logger.debug(s"checking size of unmined hashes: ${AppConst.BLOCK_MAX_SIZE} = $blockMaxSizeKb kb")

    val hashes = unminedHashes().hashes
    val size = BlockUtil.size(hashes)
    val sizeKb = size / 1000
    val maxBlockSizeBytes = Config.blockMaxSize * 1000

    size >= maxBlockSizeBytes match {

      case true =>
        logger.info(s"trigger mining of a new block (triggered by size check; blockSize: $sizeKb kb; ${hashes.size} hashes)")
        mine()

      case false => logger.debug(s"block does not need to be mined yet (size: $sizeKb kb; ${hashes.size} hashes)")

    }

  }

  private def mine(): BlockInfo = {

    val previousBlockHash = mostRecentBlock().hash
    val hashes = unminedHashes().hashes
    val block = BlockUtil.newBlock(previousBlockHash, hashes)
    val blockHash = block.hash
    logger.info(s"new block hash: $blockHash (blockSize=${BlockUtil.size(hashes) / 1000} kb; ${hashes.size} hashes)")

    upsertBlock(block)

    block

  }

}

case class SizeCheck()

case class BlockInterval()
