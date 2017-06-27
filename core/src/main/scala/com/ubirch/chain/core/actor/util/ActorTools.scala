package com.ubirch.chain.core.actor.util

import com.ubirch.chain.config.ChainConfig

import akka.routing.RoundRobinPool

/**
  * author: cvandrei
  * since: 2017-06-26
  */
trait ActorTools {

  def roundRobin(): RoundRobinPool = new RoundRobinPool(ChainConfig.akkaNumberOfWorkers)

  def sqsEndpoint(sqsQueueName: String): String = {
    s"aws-sqs://$sqsQueueName?accessKey=${ChainConfig.awsAccessKey}&secretKey=${ChainConfig.awsSecretAccessKey}"
  }

  def sqsEndpointConsumer(queue: String): String = {
    s"${sqsEndpoint(queue)}&maxMessagesPerPoll=${ChainConfig.awsSqsMaxMessagesPerPoll}"
  }

}
