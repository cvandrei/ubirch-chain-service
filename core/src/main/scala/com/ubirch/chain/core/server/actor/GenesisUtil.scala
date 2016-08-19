package com.ubirch.chain.core.server.actor

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.core.merkle.BlockUtil
import com.ubirch.client.storage.ChainStorageServiceClient
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * author: cvandrei
  * since: 2016-08-16
  */
class GenesisUtil extends LazyLogging {

  def check(): Unit = {

    logger.info("check if genesis block exists")
    ChainStorageServiceClient.getGenesisBlock map {

      case None =>
        val genesisBlock = BlockUtil.genesisBlock()
        ChainStorageServiceClient.saveGenesisBlock(genesisBlock)

      case Some(genesisBlock) => logger.info(s"genesisBlock already exists: hash=${genesisBlock.hash}")

    }

  }

}
