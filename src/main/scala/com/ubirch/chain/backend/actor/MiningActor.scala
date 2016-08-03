package com.ubirch.chain.backend.actor

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.backend.config.Config

/**
  * author: cvandrei
  * since: 2016-08-03
  */
class MiningActor extends Actor with LazyLogging {

  override def receive: Receive = {

    case sc: SizeCheck =>
      logger.info(s"ran size check: maxBlockSize=${Config.blockMaxSize} kb")
      // TODO implementation

    case m: Mine =>
      logger.info("mined a block")
      // TODO implementation

  }

}

case class SizeCheck()

case class Mine()
