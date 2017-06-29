package com.ubirch.chain.core.actor

import com.ubirch.chain.core.actor.producer.BigchainProducerActor
import com.ubirch.chain.core.actor.util.ActorTools
import com.ubirch.chain.model.rest.{DeviceMsgHashIn, DeviceMsgIn}
import com.ubirch.util.json.Json4sUtil

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
      sendToBigchainDb(deviceData)
      Json4sUtil.any2String(deviceData)

    case deviceDataHash: DeviceMsgHashIn =>

      log.debug(s"received deviceDataHash: $deviceDataHash")
      sendToBigchainDb(deviceDataHash)

    case _ => log.error("unknown message")

  }

  private def sendToBigchainDb(o: AnyRef): Unit = {

    Json4sUtil.any2String(o) match {
      case None => log.error(s"failed to convert to json: o=$o=")
      case Some(json: String) => bigchainDbProducer ! json
    }

  }

}

object BigchainActor extends ActorTools {
  def props(): Props = roundRobin().props(Props[BigchainActor])
}
