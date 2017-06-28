package com.ubirch.chain.core.manager

import com.ubirch.chain.core.actor.ActorNames
import com.ubirch.chain.core.actor.consumer.{DeviceDataHashInConsumer, DeviceDataInConsumer}

import akka.actor.ActorSystem

/**
  * author: cvandrei
  * since: 2017-06-26
  */
object QueueManager {

  def initConsumers()(implicit _system: ActorSystem): Unit = {

    _system.actorOf(DeviceDataInConsumer.props(), ActorNames.DEVICE_DATA_CONSUMER)
    _system.actorOf(DeviceDataHashInConsumer.props(), ActorNames.DEVICE_DATA_HASH_CONSUMER)

  }

}
