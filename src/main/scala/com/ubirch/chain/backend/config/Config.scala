package com.ubirch.chain.backend.config

import com.typesafe.config.ConfigFactory

/**
  * author: cvandrei
  * since: 2016-07-27
  */
object Config {

  def interface: String = ConfigFactory.load.getString(AppConst.INTERFACE)

  def port: Int = ConfigFactory.load.getInt(AppConst.PORT)

  def hashAlgorithm: String = ConfigFactory.load.getString(AppConst.HASH_ALGORITHM).toLowerCase

}
