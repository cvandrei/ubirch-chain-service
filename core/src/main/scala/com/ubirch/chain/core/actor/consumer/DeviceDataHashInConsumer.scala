package com.ubirch.chain.core.actor.consumer

import com.ubirch.chain.config.ChainConfig
import com.ubirch.chain.core.actor.util.ActorTools
import com.ubirch.chain.core.actor.{ActorNames, BigchainActor}
import com.ubirch.chain.model.rest.DeviceDataHashIn
import com.ubirch.util.json.{Json4sUtil, MyJsonProtocol}

import org.json4s.JValue

import akka.actor.{ActorLogging, Props}
import akka.camel.{CamelMessage, Consumer}

/**
  * author: cvandrei
  * since: 2017-06-28
  */
class DeviceDataHashInConsumer extends Consumer
  with ActorTools
  with ActorLogging
  with MyJsonProtocol {

  private implicit val _system = context.system
  private val bigchainActor = context.actorOf(BigchainActor.props(), ActorNames.BIGCHAIN)

  override def endpointUri: String = sqsEndpointConsumer(ChainConfig.awsSqsQueueDeviceDataHashIn)

  override def receive: Receive = {

    case msg: CamelMessage =>

      log.debug(s"received ${msg.bodyAs[String]}")
      camelMsgToDeviceDataIn(msg.body) match {

        case Some(deviceDataHashIn: DeviceDataHashIn) => bigchainActor ! deviceDataHashIn
        case None => log.error(s"invalid json message: ${msg.body}")

      }

    case _ => log.error("received unknown message")

  }

  private def camelMsgToDeviceDataIn(body: Any): Option[DeviceDataHashIn] = {

    body match {

      case txString: String =>

        Json4sUtil.string2JValue(txString) match {

          case Some(txJson: JValue) => txJson.extractOpt[DeviceDataHashIn]
          case _ => None

        }

      case _ =>
        log.error(s"received invalid message body: $body")
        None

    }

  }

}

object DeviceDataHashInConsumer {
  def props(): Props = Props[DeviceDataHashInConsumer]
}
