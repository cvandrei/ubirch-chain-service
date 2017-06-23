package com.ubirch.chain.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2017-01-19
  */
object Config extends ConfigBase {

  /**
    * The interface the server runs on.
    *
    * @return interface
    */
  def interface: String = config.getString(ConfigKeys.INTERFACE)

  /**
    * Port the server listens on.
    *
    * @return port number
    */
  def port: Int = config.getInt(ConfigKeys.PORT)

  def goPipelineName: String = config.getString(ConfigKeys.GO_PIPELINE_NAME)
  def goPipelineLabel: String = config.getString(ConfigKeys.GO_PIPELINE_LABEL)
  def goPipelineRevision: String = config.getString(ConfigKeys.GO_PIPELINE_REVISION)

  /*
   * Akka Related
   ************************************************************************************************/

  /**
    * Default actor timeout.
    *
    * @return timeout in seconds
    */
  def actorTimeout: Int = config.getInt(ConfigKeys.ACTOR_TIMEOUT)

  def akkaNumberOfWorkers: Int = config.getInt(ConfigKeys.AKKA_NUMBER_OF_WORKERS)

}
