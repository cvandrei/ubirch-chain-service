package com.ubirch.chain.backend.actor

import akka.actor.Actor
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.backend.config.{AppConst, Config}
import com.ubirch.chain.json.{Anchor, BlockInfo}
import com.ubirch.chain.merkle.BlockUtil
import com.ubirch.chain.notary.client.NotaryClient
import com.ubirch.chain.storage.ChainStorage

/**
  * author: cvandrei
  * since: 2016-08-03
  */
class MiningActor extends Actor with ChainStorage with LazyLogging {

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

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
      val block = mine()

      // TODO extract anchoring into a separate job
      anchor(block.hash) match {

        case Some(anchor) =>
          block.anchors :+ anchor
          upsertBlock(block)

        case _ => // do nothing

      }

  }

  private def mine(): BlockInfo = {

    val previousBlockHash = mostRecentBlock().hash
    val hashes = unminedHashes().hashes
    val block = BlockUtil.newBlock(previousBlockHash, hashes)
    val blockHash = block.hash
    logger.debug(s"new block hash: $blockHash (blockSize=${BlockUtil.size(hashes) / 1000} kb; ${hashes.size} hashes)")

    upsertBlock(block)

    block

  }

  private def anchor(blockHash: String): Option[Anchor] = {

    Config.anchorEnabled match {

      case false =>
        logger.info(s"anchoring is disabled (most recent blockHash: $blockHash)")
        None

      case true =>

        logger.info(s"anchoring most recent blockHash: $blockHash")

        val res = NotaryClient.notarize(blockHash)
        // TODO convert response to Option[Anchor]
        //    val notarizeResponse = response.asInstanceOf[NotarizeResponse]
        //    val anchorHash = notarizeResponse.hash
        //    val anchorType = AnchorType.bitcoin
        //    logger.info(s"anchoring was successful: blockHash=$blockHash, anchorType=$anchorType, anchorHash=$anchorHash")
        //    Anchor(anchorType, anchorHash)
        None

    }

  }

}

case class SizeCheck()

case class Mine()
