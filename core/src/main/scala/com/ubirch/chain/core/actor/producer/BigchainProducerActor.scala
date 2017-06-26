package com.ubirch.chain.core.actor.producer

import com.ubirch.chain.config.ChainConfig
import com.ubirch.chain.core.actor.ActorNames
import com.ubirch.chain.core.actor.util.ActorTools

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.camel.Producer

/**
  * author: cvandrei
  * since: 2017-06-23
  */
class BigchainProducerActor(sqsQueueName: String) extends Producer with ActorTools {

  override def endpointUri: String = sqsEndpoint(sqsQueueName)

  override def oneway: Boolean = true

}

object BigchainProducerActor extends ActorTools {

  def props(): Props = roundRobin().props(Props(new BigchainProducerActor(ChainConfig.awsSqsQueueBigchainDbOut)))

  def actor()(implicit _system: ActorSystem): ActorRef = _system.actorOf(props(), ActorNames.BIGCHAIN_PRODUCER)

}
