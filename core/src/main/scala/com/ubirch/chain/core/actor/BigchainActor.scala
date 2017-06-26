package com.ubirch.chain.core.actor

import com.ubirch.chain.core.actor.producer.BigchainProducerActor
import com.ubirch.chain.core.actor.util.ActorTools
import com.ubirch.chain.model.rest.Transaction

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

/**
  * author: cvandrei
  * since: 2017-06-23
  */
class BigchainActor extends Actor
  with ActorLogging {

  private implicit val system = context.system
  private val bigchainDbProducer = BigchainProducerActor.actor()

  override def receive: Receive = {

    case tx: Transaction =>

      log.debug(s"received tx: $tx")
      if (tx.hash.isDefined) {
        // TODO notify bigchainDbProducer
        //bigchainDbProducer ! tx.hash.get
      } else if (tx.msg.isDefined) {
        // TODO notify bigchainDbProducer
        //bigchainDbProducer ! tx.msg.get
      } else {
        log.error(s"unable to write to BigchainDb if hash and message are empty")
      }

    case _ => log.error("unknown message")

  }

}

object BigchainActor extends ActorTools {

  def props(): Props = roundRobin().props(Props(new BigchainActor()))

  def actor()(implicit _system: ActorSystem): ActorRef = _system.actorOf(props(), ActorNames.BIGCHAIN)

}
