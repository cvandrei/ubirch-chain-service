package com.ubirch.chain.core.server.routes

import com.ubirch.backend.chain.model.{BlockInfo, FullBlock, HashedData}
import com.ubirch.client.storage.ChainStorageServiceClient

import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-08-17
  */
class ExplorerUtil {

  /**
    * Gives us the BlockInfo an eventHash is included in.
    *
    * @param eventHash eventHash to search with
    * @return None if eventHash has not been mined yet; a BlockInfo otherwise
    */
  def eventHash(eventHash: String): Future[Option[BlockInfo]] = ChainStorageServiceClient.getBlockByEventHash(HashedData(eventHash))

  /**
    * Gives us the BlockInfo for the given blockHash.
    *
    * @param blockHash blockHash to search with
    * @return None if no block with given blockHash exists; a BlockInfo otherwise
    */
  def blockInfo(blockHash: String): Future[Option[BlockInfo]] = ChainStorageServiceClient.getBlockInfo(HashedData(blockHash))

  /**
    * Gives us the next BlockInfo for a given blockHash.
    *
    * @param blockHash blockHash to search with
    * @return None if no block with given blockHash exists; a BlockInfo otherwise
    */
  def nextBlockInfo(blockHash: String): Future[Option[BlockInfo]] = ChainStorageServiceClient.getNextBlockInfo(HashedData(blockHash))

  /**
    * Gives us the FullBlock for the given blockHash.
    *
    * @param blockHash blockHash to search with
    * @return None if no block with given blockHash exists; a BlockInfo otherwise
    */
  def fullBlock(blockHash: String): Future[Option[FullBlock]] = ChainStorageServiceClient.getFullBlock(blockHash)

}
