package com.ubirch.chain.backend.actor

import akka.actor.Actor
import com.ubirch.chain.core.server.actor.GenesisUtil

/**
  * author: cvandrei
  * since: 2016-08-03
  */
class GenesisActor extends Actor {

  val genesisUtil = new GenesisUtil

  override def receive: Receive = {

    case GenesisCheck => genesisUtil.check()

  }

}

case class GenesisCheck()
