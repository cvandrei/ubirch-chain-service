package com.ubirch.chain.backend.actor

import akka.actor.Actor
import com.ubirch.chain.core.server.actor.AnchorUtil

/**
  * author: cvandrei
  * since: 2016-08-08
  */
class AnchorActor extends Actor {

  val anchorUtil = new AnchorUtil

  override def receive: Receive = {

    case an: AnchorNow => anchorUtil.anchorNow()

  }

}

case class AnchorNow()
