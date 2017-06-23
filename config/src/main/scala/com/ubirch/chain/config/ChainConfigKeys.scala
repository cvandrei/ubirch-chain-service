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

}
