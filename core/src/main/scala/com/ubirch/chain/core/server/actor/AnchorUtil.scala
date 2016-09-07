package com.ubirch.chain.core.server.actor

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.backend.chain.model.{Anchor, AnchorType, FullBlock}
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

    // TODO tests
    ChainStorageServiceClient.mostRecentBlock() map {

      case None =>
        logger.error("found no most recent block")
        false

      case Some(blockInfo) => blockInfo.anchors.isEmpty match {

        case false =>
          logger.info("most recent block has been anchored already")
          false

        case true =>
          anchor(blockInfo.hash) match {

            case Some(anchor) =>
              ChainStorageServiceClient.getFullBlock(blockInfo.hash) map {

                case None =>

                case Some(fullBlock) =>
                  fullBlock.anchors :+ anchor
                  ChainStorageServiceClient.upsertFullBlock(fullBlock)
                  // TODO wait till upsert has been indexed --> otherwise we might anchor more than once
                  anchorPreviousBlocks(fullBlock)

              }
              true

            case _ => false // do nothing

          }

      }

    }

  }

  def anchor(blockHash: String): Option[Anchor] = {

    // TODO tests
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

  def anchorPreviousBlocks(block: FullBlock) = {

    // TODO tests
    val previousBlocks = loadPreviousWithoutAnchor(block)
    addAnchorToPreviousBlocks(previousBlocks, block.anchors)

  }


  def loadPreviousWithoutAnchor(block: FullBlock, list: Seq[FullBlock] = Seq.empty): Future[Seq[FullBlock]] = {

    // TODO tests
    ChainStorageServiceClient.getFullBlock(block.previousBlockHash) map {

      case None => list

      case Some(previous) =>
        previous.anchors.isEmpty match {
          case true => list
          case false =>
            loadPreviousWithoutAnchor(previous, list :+ previous)
        }

    }

  }

  private def addAnchorToPreviousBlocks(blocksWithoutAnchor: Future[Seq[FullBlock]], anchors: Seq[Anchor]): Unit = {

    // TODO tests
    blocksWithoutAnchor map { seq =>

      seq foreach { block =>

        block.anchors ++ anchors
        ChainStorageServiceClient.upsertFullBlock(block)
        Some(block)

      }

    }

  }

}
