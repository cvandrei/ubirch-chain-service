package com.ubirch.chain.core.server.actor

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.backend.chain.model.{Anchor, AnchorType}
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.notary.client.NotaryClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-08-16
  */
class AnchorUtil extends LazyLogging {

  def anchorNow(): Future[Boolean] = {

    // TODO write automated tests
    ChainStorageServiceClient.mostRecentBlock() map {

      case None =>
        logger.error("found no most recent block")
        false

      case Some(block) => block.anchors.isEmpty match {

        case false =>
          logger.info("most recent block has been anchored already")
          false

        case true =>
          anchor(block.hash) match {

            case Some(anchor) =>
              block.anchors :+ anchor
              ChainStorageServiceClient.upsertBlock(block)
              // TODO wait till upsert has been indexed --> otherwise we might anchor more than once
              true

            case _ => false // do nothing

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
