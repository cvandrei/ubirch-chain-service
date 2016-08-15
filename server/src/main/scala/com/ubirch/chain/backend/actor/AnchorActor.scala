package com.ubirch.chain.backend.actor

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.backend.config.Config
import com.ubirch.chain.json.{Anchor, AnchorType}
import com.ubirch.chain.storage.ChainStorage
import com.ubirch.notary.client.NotaryClient

/**
  * author: cvandrei
  * since: 2016-08-08
  */
class AnchorActor extends Actor
  with ChainStorage
  with LazyLogging {

  override def receive: Receive = {

    case an: AnchorNow =>

      Config.anchorEnabled match {

        case false => logger.info(s"anchoring is disabled in configuration")

        case true =>

          val block = mostRecentBlock()
          block.anchors.isEmpty match {

            case true =>

              anchor(block.hash) match {

                case Some(anchor) =>
                  block.anchors :+ anchor
                  upsertBlock(block)

                case _ => // do nothing

              }

            case false => logger.info("most recent block has been anchored already")

          }

      }

  }

  private def anchor(blockHash: String): Option[Anchor] = {

      logger.info(s"anchoring most recent blockHash: $blockHash")

      NotaryClient.notarize(blockHash) match {

        case Some(notarizeResponse) =>
          val anchorHash = notarizeResponse.hash
          val anchorType = AnchorType.bitcoin
          logger.info(s"anchoring was successful: blockHash=$blockHash, anchorType=$anchorType, anchorHash=$anchorHash")
          Some(Anchor(anchorType, anchorHash))

        case None => None

      }

  }

}

case class AnchorNow()
