package com.ubirch.chain.core.actor.producer

import com.ubirch.chain.config.ChainConfig

import akka.actor.{Actor, Props}
import akka.camel.Producer

/**
  * author: cvandrei
  * since: 2017-06-23
  */
class BigchainProducerActor(sqsQueueName: String) extends Actor with Producer {

  private val accessKey = ChainConfig.awsAccessKey

  private val secretKey = ChainConfig.awsSecretAccessKey

  override def endpointUri = s"aws-sqs://$sqsQueueName?accessKey=$accessKey&secretKey=$secretKey"

  override def oneway: Boolean = true

}

object BigchainProducerActor {
  def props(sqsQueueName: String): Props = Props(new BigchainProducerActor(sqsQueueName))
}
