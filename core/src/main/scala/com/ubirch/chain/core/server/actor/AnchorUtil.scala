package com.ubirch.chain.core.server.actor

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.backend.chain.model.{Anchor, AnchorType, FullBlock}
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.notary.client.NotaryClient

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

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
                  addAnchorToPreviousBlocks(fullBlock)

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

  def addAnchorToPreviousBlocks(block: FullBlock): Unit = {
    // TODO tests
    val previousBlocks = loadPreviousWithoutAnchor(block)
    updatePreviousBlocksWithAnchor(previousBlocks, block.anchors)
  }

  // TODO tests
  def loadPreviousWithoutAnchor(currentBlock: FullBlock): Seq[FullBlock] = addPreviousBlocks(Seq(currentBlock)).tail

  @tailrec
  private def addPreviousBlocks(blockList: Seq[FullBlock]): Seq[FullBlock] = {

    val lastBlockHash = blockList.last.hash
    // TODO refactor to not needing Await.result()
    val newListOpt = Await.result(ChainStorageServiceClient.getFullBlock(lastBlockHash), 10 seconds) match {

      case Some(previous) if previous.anchors.isEmpty =>
        Some(blockList :+ previous)

      case _ => None

    }

    newListOpt match {
      case None => blockList
      case Some(list) => addPreviousBlocks(list)
    }

  }

  def updatePreviousBlocksWithAnchor(blocksWithoutAnchor: Seq[FullBlock], anchors: Seq[Anchor]): Unit = {

    // TODO tests
    blocksWithoutAnchor foreach { block =>

      block.anchors ++ anchors
      ChainStorageServiceClient.upsertFullBlock(block)
      Some(block)

    }

  }

}
