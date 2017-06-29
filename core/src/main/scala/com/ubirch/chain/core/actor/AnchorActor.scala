package com.ubirch.chain.core.actor

import com.ubirch.chain.config.ChainConfig

import akka.actor.{Actor, ActorLogging, Props}

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-06-29
  */
class AnchorActor extends Actor with ActorLogging {

  implicit private val ec = context.dispatcher

  override def receive: Receive = {

    case _: BlockCheck =>

      log.debug("checking if we have a block that needs to be anchored")
      // TODO implement

      val anchorInterval = ChainConfig.anchorInterval
      log.debug(s"rescheduling AnchorActor to run in $anchorInterval seconds")
      context.system.scheduler.scheduleOnce(anchorInterval seconds, self, BlockCheck())

    case _ => log.error("unknown message")

  }

}

object AnchorActor extends {
  def props(): Props = Props[AnchorActor]
}

case class BlockCheck()
