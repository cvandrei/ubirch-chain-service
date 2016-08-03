package com.ubirch.chain.backend.actor

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

/**
  * author: cvandrei
  * since: 2016-08-03
  */
class GenesisActor extends Actor with LazyLogging {

  override def receive: Receive = {

    case GenesisCheck =>
      logger.info("check if genesis block exists")
      // TODO implementation

  }

}

case class GenesisCheck()
