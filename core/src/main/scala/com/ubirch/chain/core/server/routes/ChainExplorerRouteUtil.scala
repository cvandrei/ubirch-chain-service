package com.ubirch.chain.core.server.routes

import com.ubirch.backend.chain.model.{BlockInfo, FullBlock, Hash}
import com.ubirch.client.storage.ChainStorageServiceClient

import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-08-17
  */
class ChainExplorerRouteUtil {

  def hash(hash: String): Future[Option[FullBlock]] = ChainStorageServiceClient.getBlockByEventHash(Hash(hash))

  def block(hash: String): Future[Option[BlockInfo]] = ChainStorageServiceClient.getBlockInfo(Hash(hash))

  def fullBlock(hash: String): Future[Option[FullBlock]] = ChainStorageServiceClient.getFullBlock(hash)

}
