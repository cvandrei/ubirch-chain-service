package com.ubirch.chain.core.manager

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.chain.model.db.{Anchor, BlockInfo}
import com.ubirch.notary.client.NotaryClient
import com.ubirch.util.mongo.connection.MongoUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-06-30
  */
object AnchorManager extends StrictLogging {

  def blockCheckAndAnchor()(mongoBigchain: MongoUtil, mongoChain: MongoUtil): Future[Option[BlockInfo]] = {

    // TODO automated tests
    blockCheck()(mongoBigchain = mongoBigchain, mongoChain = mongoChain) flatMap {

      case None => Future(None)
      case Some(blockHash) => anchor(blockHash)(mongoChain)

    }

  }

  /**
    * Checks if we have a block to anchor.
    *
    * @param mongoBigchain connection to MongoDB used by BigchainDb
    * @param mongoChain    connection to MongoDB we keep track of anchored blocks with
    * @return blockHash to anchor; None if no anchoring is necessary
    */
  def blockCheck()(implicit mongoBigchain: MongoUtil, mongoChain: MongoUtil): Future[Option[String]] = {

    // TODO automated tests
    logger.debug("checking if we have a block that needs to be anchored")
    BigchainManager.findMostRecentBlockInfo()(mongoBigchain) flatMap {

      case None =>
        logger.info("found no latest BigchainDb block")
        Future(None)

      case Some(blockInfo) =>

        logger.debug(s"found a latest BigchainDb block: $blockInfo")
        val blockHash = blockInfo.id
        BlockInfoManager.findByBlockHash(blockHash)(mongoChain) map {

          case None => Some(blockHash)

          case Some(_) =>
            logger.info(s"most recent BigchainDb block has been anchored already")
            None

        }

    }

  }

  /**
    * Anchor a block hash through the notary-service.
    *
    * @param blockHash blockHash to anchor
    * @param mongo     connection to MongoDB we keep track of anchored blocks with
    * @return
    */
  def anchor(blockHash: String)(implicit mongo: MongoUtil): Future[Option[BlockInfo]] = {

    // TODO automated tests
    logger.debug(s"anchoring: blockHash=$blockHash")
    NotaryClient.notarize(blockHash, dataIsHash = true) match {

      case None =>

        logger.error(s"failed to anchor: blockHash=$blockHash")
        Future(None)

      case Some(notarizeResponse) =>

        val anchor = Anchor(hash = notarizeResponse.hash)
        val blockAnchor = BlockInfo(
          blockHash = blockHash,
          anchors = Set(anchor)
        )
        logger.info(s"anchoring was successful: blockAnchor=$blockAnchor")
        BlockInfoManager.create(blockAnchor)

    }

  }

}
