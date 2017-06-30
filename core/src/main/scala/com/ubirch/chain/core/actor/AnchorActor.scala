package com.ubirch.chain.core.actor

import com.ubirch.chain.config.ChainConfig
import com.ubirch.chain.core.manager.AnchorManager
import com.ubirch.util.mongo.connection.MongoUtil

import akka.actor.{Actor, ActorLogging, Props}

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-06-29
  */
class AnchorActor(implicit mongoBigchain: MongoUtil, mongoChain: MongoUtil)
  extends Actor with ActorLogging {

  implicit private val ec = context.dispatcher

  override def receive: Receive = {

    case _: BlockCheck =>

      AnchorManager.blockCheckAndAnchor()(mongoBigchain = mongoBigchain, mongoChain = mongoChain)
      reschedule()

    case _ => log.error("unknown message")

  }

  private def reschedule(): Unit = {

    val anchorInterval = ChainConfig.anchorInterval
    log.debug(s"rescheduling AnchorActor to run in $anchorInterval seconds")
    context.system.scheduler.scheduleOnce(anchorInterval seconds, self, BlockCheck())

  }

}

object AnchorActor extends {

  def props()(implicit mongoBigchain: MongoUtil, mongoChain: MongoUtil): Props = {

    Props(
      new AnchorActor()(mongoBigchain = mongoBigchain, mongoChain = mongoChain)
    )

  }

}

case class BlockCheck()
