package com.ubirch.chain.core.config

/**
  * author: cvandrei
  * since: 2016-07-26
  */
object ConfigKeys {

  private final val prefix = "ubirchChainService"

  final val INTERFACE = s"$prefix.interface"
  final val PORT = s"$prefix.port"

  final val BLOCK_CHECK_INTERVAL = s"$prefix.block.checkInterval"
  final val BLOCK_MAX_SIZE = s"$prefix.block.maxSize"
  final val BLOCK_MINE_EVERY_X_SECONDS = s"$prefix.block.mineEveryXSeconds"

  final val ANCHOR_ENABLED = s"$prefix.anchor.enabled"
  final val ANCHOR_INTERVAL = s"$prefix.anchor.interval"

}
