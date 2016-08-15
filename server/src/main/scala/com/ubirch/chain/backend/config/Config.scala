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
    * Tells us how often we check if a new block may be mined
    *
    * @return number of seconds
    */
  def blockCheckInterval: Int = ConfigFactory.load.getInt(AppConst.BLOCK_CHECK_INTERVAL)

  /**
    * Maximum size of blocks in kilobytes.
    *
    * @return max block size in kilobytes
    */
  def blockMaxSize: Int = ConfigFactory.load.getInt(AppConst.BLOCK_MAX_SIZE)

  /**
    * @return maximum number of seconds after which we mine a block
    */
  def mineEveryXSeconds: Int = ConfigFactory.load.getInt(AppConst.BLOCK_MINE_EVERY_X_SECONDS)

  /**
    * @return true if anchoring is enabled (which chains depends on Notary Service)
    */
  def anchorEnabled: Boolean = ConfigFactory.load.getBoolean(AppConst.ANCHOR_ENABLED)

  /**
    * @return interval (in seconds) between two anchors
    */
  def anchorInterval: Int = ConfigFactory.load.getInt(AppConst.ANCHOR_INTERVAL)

}
