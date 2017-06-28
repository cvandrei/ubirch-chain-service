package com.ubirch.chain.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2017-01-19
  */
object ChainConfig extends ConfigBase {

  /**
    * The interface the server runs on.
    *
    * @return interface
    */
  def interface: String = config.getString(ChainConfigKeys.INTERFACE)

  /**
    * Port the server listens on.
    *
    * @return port number
    */
  def port: Int = config.getInt(ChainConfigKeys.PORT)

  def goPipelineName: String = config.getString(ChainConfigKeys.GO_PIPELINE_NAME)

  def goPipelineLabel: String = config.getString(ChainConfigKeys.GO_PIPELINE_LABEL)

  def goPipelineRevision: String = config.getString(ChainConfigKeys.GO_PIPELINE_REVISION)

  /*
   * Akka Related
   ************************************************************************************************/

  /**
    * Default actor timeout.
    *
    * @return timeout in seconds
    */
  def actorTimeout: Int = config.getInt(ChainConfigKeys.ACTOR_TIMEOUT)

  def akkaNumberOfWorkers: Int = config.getInt(ChainConfigKeys.AKKA_NUMBER_OF_WORKERS)

  /*
   * AWS Related
   ************************************************************************************************/

  def awsAccessKey: String = config.getString(ChainConfigKeys.AWS_ACCESS_KEY)

  def awsSecretAccessKey: String = config.getString(ChainConfigKeys.AWS_SECRET_ACCESS_KEY)

  def awsSqsConcurrentConsumers: Int = config.getInt(ChainConfigKeys.AWS_SQS_CONCURRENT_CONSUMERS)

  def awsSqsMaxMessagesPerPoll: Int = config.getInt(ChainConfigKeys.AWS_SQS_MAX_MESSAGES_PER_POLL)

  def awsSqsQueueTransactionsIn: String = config.getString(ChainConfigKeys.AWS_SQS_QUEUE_TRANSACTION_IN)

  def awsSqsQueueBigchainDbIn: String = config.getString(ChainConfigKeys.AWS_SQS_QUEUE_BIGCHAIN_DB_IN)

}
