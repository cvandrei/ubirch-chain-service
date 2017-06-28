package com.ubirch.chain.core.actor

import com.ubirch.chain.core.actor.producer.BigchainProducerActor
import com.ubirch.chain.core.actor.util.ActorTools
import com.ubirch.chain.model.rest.{DeviceMsgHashIn, DeviceMsgIn}

import akka.actor.{Actor, ActorLogging, Props}

/**
  * author: cvandrei
  * since: 2017-06-23
  */
class BigchainActor extends Actor
  with ActorLogging {

  private implicit val system = context.system
  private val bigchainDbProducer = context.actorOf(BigchainProducerActor.props(), ActorNames.BIGCHAIN_PRODUCER)

  override def receive: Receive = {

    case deviceData: DeviceMsgIn =>

      log.debug(s"received deviceData: $deviceData")
      bigchainDbProducer ! deviceData // TODO send JSON

    case deviceDataHash: DeviceMsgHashIn =>

      log.debug(s"received deviceDataHash: $deviceDataHash")
      bigchainDbProducer ! deviceDataHash // TODO send JSON

    case _ => log.error("unknown message")

  }

}

object BigchainActor extends ActorTools {
  def props(): Props = roundRobin().props(Props[BigchainActor])
}
