package com.ubirch.chain.backend.config

import com.typesafe.config.ConfigFactory

/**
  * author: cvandrei
  * since: 2016-07-27
  */
object Config {

  /**
    * The interface the server runs on.
    *
    * @return interface
    */
  def interface: String = ConfigFactory.load.getString(AppConst.INTERFACE)

  /**
    * Port the server listens on.
    *
    * @return port number
    */
  def port: Int = ConfigFactory.load.getInt(AppConst.PORT)

  /**
    * Maximum size of blocks in kilobytes.
    *
    * @return max block size in kilobytes
    */
  def blockMaxSize: Int = ConfigFactory.load.getInt(AppConst.BLOCK_MAX_SIZE)

  /**
    * The interval (in seconds) in which we check if the maximum block size has been reached.
    *
    * @return number of seconds between block size checks
    */
  def blockSizeCheckInterval: Int = ConfigFactory.load.getInt(AppConst.BLOCK_SIZE_CHECK_INTERVAL)

  /**
    * The maximum interval (in seconds) after which we "mine" a new block no matter how small it might be.
    *
    * @return block interval in seconds
    */
  def blockInterval: Int = ConfigFactory.load.getInt(AppConst.BLOCK_INTERVAL)

}
