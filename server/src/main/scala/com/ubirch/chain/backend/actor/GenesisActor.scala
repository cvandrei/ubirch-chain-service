package com.ubirch.chain.backend.actor

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.merkle.BlockUtil
import com.ubirch.chain.storage.ChainStorage

/**
  * author: cvandrei
  * since: 2016-08-03
  */
class GenesisActor extends Actor with ChainStorage with LazyLogging {

  override def receive: Receive = {

    case GenesisCheck =>
      logger.info("check if genesis block exists")
      getGenesisBlock match {

        case None =>
          val genesisBlock = BlockUtil.genesisBlock()
          saveGenesisBlock(genesisBlock)

        case Some(genesisBlock) => logger.info(s"genesisBlock already exists: hash=${genesisBlock.hash}")
      }

  }

}

case class GenesisCheck()
