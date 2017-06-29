package com.ubirch.chain.config

/**
  * author: cvandrei
  * since: 2017-01-19
  */
object ChainConfigKeys {

  final val CONFIG_PREFIX = "ubirchChainService"

  /*
   * general server configs
   *********************************************************************************************/

  final val INTERFACE = s"$CONFIG_PREFIX.interface"
  final val PORT = s"$CONFIG_PREFIX.port"

  final val GO_PIPELINE_NAME = s"$CONFIG_PREFIX.gopipelinename"
  final val GO_PIPELINE_LABEL = s"$CONFIG_PREFIX.gopipelinelabel"
  final val GO_PIPELINE_REVISION = s"$CONFIG_PREFIX.gopipelinerev"

  /*
   * Akka related configs
   *********************************************************************************************/

  private val akkaPrefix = s"$CONFIG_PREFIX.akka"

  final val ACTOR_TIMEOUT = s"$akkaPrefix.actorTimeout"
  final val AKKA_NUMBER_OF_WORKERS = s"$akkaPrefix.numberOfWorkers"

  /*
   * AWS related configs
   *********************************************************************************************/

  private val awsPrefix = s"$CONFIG_PREFIX.aws"

  final val AWS_ACCESS_KEY = s"$awsPrefix.awsaccesskey"
  final val AWS_SECRET_ACCESS_KEY = s"$awsPrefix.awssecretaccesskey"
  final val AWS_REGION = s"$awsPrefix.region"
  final val AWS_QUEUE_OWNER_ID = s"$awsPrefix.queueOwnerId"

  private val awsSqs = s"$awsPrefix.sqs"

  final val AWS_SQS_CONCURRENT_CONSUMERS = s"$awsSqs.concurrentConsumers"
  final val AWS_SQS_MAX_MESSAGES_PER_POLL = s"$awsSqs.maxMessagesPerPoll"

  private val awsSqsQueues = s"$awsSqs.queues"

  final val AWS_SQS_QUEUE_DEVICE_DATA_IN = s"$awsSqsQueues.deviceDataIn"
  final val AWS_SQS_QUEUE_DEVICE_DATA_HASH_IN = s"$awsSqsQueues.deviceDataHashIn"

  final val AWS_SQS_QUEUE_BIGCHAIN_DB_IN = s"$awsSqsQueues.bigchainDbIn"

  /*
   * Blockchain Anchoring Related
   *********************************************************************************************/

  private val anchor = s"$CONFIG_PREFIX.anchor"
  final val ANCHOR_SCHEDULER_OFFSET = s"$anchor.schedulerOffset"
  final val ANCHOR_INTERVAL = s"$anchor.interval"
  final val ANCHOR_ENABLED = s"$anchor.enabled"

  /*
   * Mongo (BigchainDb)
   *********************************************************************************************/

  final val MONGO_BIGCHAIN_PREFIX = s"$CONFIG_PREFIX.mongoBigchain"
  private final val mongoBigchainCollection = s"$MONGO_BIGCHAIN_PREFIX.collection"

  final val BIGCHAIN_COLLECTION_BIGCHAIN = s"$mongoBigchainCollection.bigchain"

}
