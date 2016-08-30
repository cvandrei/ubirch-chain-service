package com.ubirch.chain.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2016-07-27
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

  /**
    * Tells us how often we check if a new block may be mined
    *
    * @return number of seconds
    */
  def blockCheckInterval: Int = config.getInt(ConfigKeys.BLOCK_CHECK_INTERVAL)

  /**
    * Maximum size of blocks in kilobytes.
    *
    * @return max block size in kilobytes
    */
  def blockMaxSizeKB: Long = config.getLong(ConfigKeys.BLOCK_MAX_SIZE)

  def blockMaxSizeByte: Long = blockMaxSizeKB * 1000

  /**
    * @return maximum number of seconds after which we mine a block
    */
  def mineEveryXSeconds: Int = config.getInt(ConfigKeys.BLOCK_MINE_EVERY_X_SECONDS)

  /**
    * @return true if anchoring is enabled (which chains depends on Notary Service)
    */
  def anchorEnabled: Boolean = config.getBoolean(ConfigKeys.ANCHOR_ENABLED)

  /**
    * @return interval (in seconds) between two anchors
    */
  def anchorInterval: Int = config.getInt(ConfigKeys.ANCHOR_INTERVAL)

}
