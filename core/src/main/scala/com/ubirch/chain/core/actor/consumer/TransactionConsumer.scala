package com.ubirch.chain.core.actor.consumer

import com.ubirch.chain.config.ChainConfig
import com.ubirch.chain.core.actor.util.ActorTools
import com.ubirch.chain.core.actor.{ActorNames, BigchainActor}
import com.ubirch.chain.model.rest.Transaction
import com.ubirch.util.json.{Json4sUtil, MyJsonProtocol}

import org.json4s.JValue

import akka.actor.{ActorLogging, ActorRef, ActorSystem, Props}
import akka.camel.{CamelMessage, Consumer}

/**
  * author: cvandrei
  * since: 2017-06-26
  */
class TransactionConsumer extends Consumer
  with ActorTools
  with ActorLogging
  with MyJsonProtocol {

  private implicit val _system = context.system
  private val bigchainActor = BigchainActor.actor()

  override def endpointUri: String = sqsEndpointConsumer(ChainConfig.awsSqsQueueTransactionsIn)

  override def receive: Receive = {

    case msg: CamelMessage =>

      log.debug(s"received ${msg.bodyAs[String]}")
      camelMsgToTransaction(msg.body) match {

        case Some(tx: Transaction) => bigchainActor ! tx
        case None => log.error(s"invalid json message: ${msg.body}")

      }

    case _ => log.error("received unknown message")

  }

  private def camelMsgToTransaction(body: Any): Option[Transaction] = {

    body match {

      case txString: String =>

        Json4sUtil.string2JValue(txString) match {

          case Some(txJson: JValue) => txJson.extractOpt[Transaction]
          case _ => None

        }

      case _ =>
        log.error(s"received invalid message body: $body")
        None

    }

  }

}

object TransactionConsumer extends ActorTools {

  def props(): Props = roundRobin().props(Props(new TransactionConsumer()))

  def actor()(implicit _system: ActorSystem): ActorRef = _system.actorOf(props(), ActorNames.TRANSACTION_CONSUMER)

}
