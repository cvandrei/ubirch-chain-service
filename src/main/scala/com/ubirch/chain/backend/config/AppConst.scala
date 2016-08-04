package com.ubirch.chain.backend.config

/**
  * author: cvandrei
  * since: 2016-07-26
  */
object AppConst {

  private final val prefix = "ubirchChainService"

  final val INTERFACE = s"$prefix.interface"
  final val PORT = s"$prefix.port"

  final val BLOCK_MAX_SIZE = s"$prefix.block.maxSize"
  final val BLOCK_SIZE_CHECK_INTERVAL = s"$prefix.block.sizeCheckInterval"
  final val BLOCK_INTERVAL = s"$prefix.block.interval"

  final val ANCHOR_ENABLED = s"$prefix.anchor.enabled"
  final val ANCHOR_URL = s"$prefix.anchor.url"

}
