package com.ubirch.chain.core.actor

import com.ubirch.chain.model.rest.Transaction

import akka.actor.{Actor, ActorLogging}

/**
  * author: cvandrei
  * since: 2017-06-23
  */
class BigchainActor extends Actor
  with ActorLogging {

  override def receive: Receive = {

    case tx: Transaction =>
      log.debug(s"received tx: $tx")
      // TODO

    case _ => log.error("unknown message")

  }

}
