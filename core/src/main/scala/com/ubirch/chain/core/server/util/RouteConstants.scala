package com.ubirch.chain.core.server.util

/**
  * author: cvandrei
  * since: 2016-07-26
  */
object RouteConstants {

  val api = "api"
  val v1 = "v1"
  val chainService = "chainService"
  val hash = "hash"
  val explorer = "explorer"
  val eventHash = "eventHash"
  val blockInfo = "blockInfo"
  val blockInfoByPrevious = "blockInfoByPrevious"
  val fullBlock = "fullBlock"

  val urlPrefix = s"/$api/$v1/$chainService"
  val urlHash = s"$urlPrefix/$hash"

  val urlExplorerPrefix = s"$urlPrefix/$explorer"

  val urlExplorerEventHashPrefix = s"$urlExplorerPrefix/$eventHash"
  def urlExplorerEventHash(eventHash: String) = s"$urlExplorerEventHashPrefix/$eventHash"

  val urlExplorerBlockInfoPrefix = s"$urlExplorerPrefix/$blockInfo"
  def urlExplorerBlockInfo(blockHash: String) = s"$urlExplorerBlockInfoPrefix/$blockHash"

  val urlExplorerBlockInfoByPreviousPrefix = s"$urlExplorerPrefix/$blockInfoByPrevious"
  def urlExplorerBlockInfoByPrevious(blockHash: String) = s"$urlExplorerBlockInfoByPreviousPrefix/$blockHash"

  val urlExplorerFullBlockPrefix = s"$urlExplorerPrefix/$fullBlock"
  def urlExplorerFullBlock(blockHash: String) = s"$urlExplorerFullBlockPrefix/$blockHash"

}
