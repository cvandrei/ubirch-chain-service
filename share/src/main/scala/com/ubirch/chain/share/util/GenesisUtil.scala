package com.ubirch.chain.share.util

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.share.merkle.BlockUtil
import com.ubirch.client.storage.ChainStorageServiceClient

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps


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
        waitUntilGenesisIndexed()

      case Some(genesisBlock) => logger.info(s"genesisBlock already exists: hash=${genesisBlock.hash}")

    }

  }

  def waitUntilGenesisIndexed(): Unit = {

    var genesisOpt = Await.result(ChainStorageServiceClient.getGenesisBlock, 10 seconds)
    while (genesisOpt.isEmpty) {

      logger.debug("genesis block indexing...still waiting")
      Thread.sleep(100)
      genesisOpt = Await.result(ChainStorageServiceClient.getGenesisBlock, 10 seconds)

    }

    logger.info("genesis block indexing...done")

  }

}
