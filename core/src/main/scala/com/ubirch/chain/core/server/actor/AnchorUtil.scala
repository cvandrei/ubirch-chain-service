package com.ubirch.chain.core.server.actor

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.backend.chain.model.{Anchor, AnchorType}
import com.ubirch.chain.config.Config
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.notary.client.NotaryClient

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * author: cvandrei
  * since: 2016-08-16
  */
class AnchorUtil extends LazyLogging {

  def anchorNow(): Unit = {

    Config.anchorEnabled match {

      case false => logger.info(s"anchoring is disabled in configuration")

      case true =>

        ChainStorageServiceClient.mostRecentBlock() map {

          case None => logger.error("found no most recent block")

          case Some(block) => block.anchors.isEmpty match {

            case false => logger.info("most recent block has been anchored already")

            case true =>
              anchor(block.hash) match {

                case Some(anchor) =>
                  block.anchors :+ anchor
                  ChainStorageServiceClient.upsertBlock(block)

                case _ => // do nothing

              }

          }

        }

    }

  }

  private def anchor(blockHash: String): Option[Anchor] = {

    logger.info(s"anchoring most recent blockHash: $blockHash")

    NotaryClient.notarize(blockHash, dataIsHash = true) match {

      case Some(notarizeResponse) =>
        val anchorHash = notarizeResponse.hash
        val anchorType = AnchorType.bitcoin
        logger.info(s"anchoring was successful: blockHash=$blockHash, anchorType=$anchorType, anchorHash=$anchorHash")
        Some(Anchor(anchorType, anchorHash))

      case None => None

    }

  }

}
