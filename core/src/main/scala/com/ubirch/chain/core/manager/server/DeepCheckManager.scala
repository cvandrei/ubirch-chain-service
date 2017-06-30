package com.ubirch.chain.core.manager.server

import com.ubirch.chain.config.ChainConfig
import com.ubirch.chain.model.db.{Anchor, BlockInfo}
import com.ubirch.chain.model.db.bigchain.BigchainBlockInfo
import com.ubirch.util.deepCheck.model.DeepCheckResponse
import com.ubirch.util.mongo.connection.MongoUtil
import com.ubirch.util.mongo.format.MongoFormats

import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros}

import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-06-09
  */
object DeepCheckManager extends MongoFormats {

  implicit protected def anchorReader: BSONDocumentReader[Anchor] = Macros.reader[Anchor]
  implicit protected def anchorWriter: BSONDocumentWriter[Anchor] = Macros.writer[Anchor]
  implicit protected def blockInfoReader: BSONDocumentReader[BlockInfo] = Macros.reader[BlockInfo]
  implicit protected def blockInfoWriter: BSONDocumentWriter[BlockInfo] = Macros.writer[BlockInfo]

  implicit protected def bigchainBlockInfoReader: BSONDocumentReader[BigchainBlockInfo] = Macros.reader[BigchainBlockInfo]
  implicit protected def bigchainBlockInfoWriter: BSONDocumentWriter[BigchainBlockInfo] = Macros.writer[BigchainBlockInfo]

  def connectivityCheck()(implicit mongoBigchain: MongoUtil, mongoChain: MongoUtil): Future[DeepCheckResponse] = {

    // TODO activate bigchain connectivity check
    //mongoBigchain.connectivityCheck[BigchainBlockInfo](ChainConfig.mongoBigchainCollectionBigchain)

    mongoChain.connectivityCheck[BigchainBlockInfo](ChainConfig.mongoBigchainCollectionBigchain)

  }

}
