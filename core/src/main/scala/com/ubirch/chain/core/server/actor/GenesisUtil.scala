package com.ubirch.chain.core.server.actor

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.core.merkle.BlockUtil
import com.ubirch.chain.core.storage.ChainStorage

/**
  * author: cvandrei
  * since: 2016-08-16
  */
class GenesisUtil extends ChainStorage
  with LazyLogging {

  def check(): Unit = {

    logger.info("check if genesis block exists")
    getGenesisBlock match {

      case None =>
        val genesisBlock = BlockUtil.genesisBlock()
        saveGenesisBlock(genesisBlock)

      case Some(genesisBlock) => logger.info(s"genesisBlock already exists: hash=${genesisBlock.hash}")

    }

  }

}
