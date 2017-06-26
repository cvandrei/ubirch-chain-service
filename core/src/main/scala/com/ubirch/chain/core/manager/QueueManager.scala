package com.ubirch.chain.core.manager

import com.ubirch.chain.core.actor.consumer.TransactionConsumer

import akka.actor.ActorSystem

/**
  * author: cvandrei
  * since: 2017-06-26
  */
object QueueManager {

  def initConsumers()(implicit _system: ActorSystem): Unit = {

    TransactionConsumer.actor()

  }

}
