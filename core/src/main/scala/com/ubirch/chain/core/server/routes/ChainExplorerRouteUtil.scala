package com.ubirch.chain.core.server.routes

import com.ubirch.backend.chain.model.{BlockInfo, FullBlock, Hash}
import com.ubirch.client.storage.ChainStorageServiceClient._

import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-08-17
  */
class ChainExplorerRouteUtil {

  def hash(hash: String): Future[Option[FullBlock]] = getBlockByEventHash(Hash(hash))

  def block(hash: String): Future[Option[BlockInfo]] = getBlockInfo(Hash(hash))

  def fullBlock(hash: String): Future[Option[FullBlock]] = getFullBlock(hash)

}
