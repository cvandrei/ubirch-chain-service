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
object BlockManager extends MongoFormats
  with StrictLogging {

  private val collectionName = ChainConfig.mongoBigchainCollectionBigchain

  implicit protected def contextReader: BSONDocumentReader[BigchainBlockInfo] = Macros.reader[BigchainBlockInfo]

  def findLatestBlockInfo()(implicit mongo: MongoUtil): Future[Option[BigchainBlockInfo]] = {

    // TODO automated tests
    val selector = document()
    val sort = document("block.timestamp" -> -1)
    mongo.collection(collectionName) flatMap {
      _.find(selector).sort(sort).one[BigchainBlockInfo]
    }

  }

}
