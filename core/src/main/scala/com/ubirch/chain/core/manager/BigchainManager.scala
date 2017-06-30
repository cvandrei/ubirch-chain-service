package com.ubirch.chain.core.manager

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.chain.config.ChainConfig
import com.ubirch.chain.model.db.bigchain.BigchainBlockInfo
import com.ubirch.util.mongo.connection.MongoUtil
import com.ubirch.util.mongo.format.MongoFormats

import reactivemongo.bson.{BSONDocumentReader, Macros, document}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-06-29
  */
object BigchainManager extends MongoFormats
  with StrictLogging {

  private val collectionName = ChainConfig.mongoBigchainCollectionBigchain
  private val emptyDoc = document()

  implicit protected def contextReader: BSONDocumentReader[BigchainBlockInfo] = Macros.reader[BigchainBlockInfo]

  /**
    * Tells us what the most recent block in BigchainDb is. This method will be obsolete once BigchainDb's stream API
    * allows us to subscribe to this information.
    *
    * @param mongo connection to MongoDB that BigchainDb relies on
    * @return info about the most recent BigchainDb block; None if none exists
    */
  def findMostRecentBlockInfo()(implicit mongo: MongoUtil): Future[Option[BigchainBlockInfo]] = {

    // TODO automated tests
    val sort = document("block.timestamp" -> -1)
    mongo.collection(collectionName) flatMap {
      _.find(emptyDoc).sort(sort).one[BigchainBlockInfo]
    }

  }

}
