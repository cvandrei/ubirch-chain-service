package com.ubirch.chain.core.manager

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.chain.config.ChainConfig
import com.ubirch.chain.model.db.{Anchor, BlockInfo}
import com.ubirch.util.mongo.connection.MongoUtil
import com.ubirch.util.mongo.format.MongoFormats

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros, document}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-06-30
  */
object BlockInfoManager extends MongoFormats
  with StrictLogging {

  private val collectionName = ChainConfig.mongoChainServiceCollectionAnchors

  implicit protected def anchorReader: BSONDocumentReader[Anchor] = Macros.reader[Anchor]
  implicit protected def anchorWriter: BSONDocumentWriter[Anchor] = Macros.writer[Anchor]

  implicit protected def blockInfoReader: BSONDocumentReader[BlockInfo] = Macros.reader[BlockInfo]
  implicit protected def blockInfoWriter: BSONDocumentWriter[BlockInfo] = Macros.writer[BlockInfo]

  /**
    * Find a [BlockInfo] by the hash of a BigchainDb block.
    *
    * @param hash  block hash as used by BigchainDb
    * @param mongo connection to the MongoDB used by chain-service
    * @return [BlockInfo] with the given block hash; None if none exists
    */
  def findByBlockHash(hash: String)(implicit mongo: MongoUtil): Future[Option[BlockInfo]] = {

    // TODO automated tests
    val selector = document("blockHash" -> hash)
    mongo.collection(collectionName) flatMap {
      _.find(selector).one[BlockInfo]
    }

  }

  def createBlockAnchor(blockAnchor: BlockInfo)(implicit mongo: MongoUtil): Future[Option[BlockInfo]] = {

    // TODO automated tests
    for {

      collection <- mongo.collection(collectionName)
      findByBlockHash <- findByBlockHash(blockAnchor.blockHash)

      result <- executeInsert(collection, findByBlockHash, blockAnchor)

    } yield result

  }

  private def executeInsert(collection: BSONCollection,
                            findByBlockHash: Option[BlockInfo],
                            blockAnchor: BlockInfo
                           ): Future[Option[BlockInfo]] = {

    if (findByBlockHash.isDefined) {
      logger.error(s"unable to create blockAnchor as it already exist: blockAnchorInDb=$findByBlockHash")
      Future(None)
    } else {

      collection.insert[BlockInfo](blockAnchor) map { writeResult =>

        if (writeResult.ok) {
          logger.debug(s"created new blockAnchor: $blockAnchor")
          Some(blockAnchor)
        } else {
          logger.error("failed to create blockAnchor")
          None
        }

      }

    }

  }

}
