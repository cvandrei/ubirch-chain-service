package com.ubirch.chain.core.actor

import com.ubirch.chain.core.actor.producer.BigchainProducerActor
import com.ubirch.chain.core.actor.util.ActorTools
import com.ubirch.chain.model.rest.{DeviceDataHashIn, DeviceDataIn}

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

    case deviceData: DeviceDataIn =>

      log.debug(s"received deviceData: $deviceData")
      bigchainDbProducer ! deviceData

    case deviceDataHash: DeviceDataHashIn =>

      log.debug(s"received deviceDataHash: $deviceDataHash")
      bigchainDbProducer ! deviceDataHash

    case _ => log.error("unknown message")

  }

}

object BigchainActor extends ActorTools {
  def props(): Props = roundRobin().props(Props[BigchainActor])
}
