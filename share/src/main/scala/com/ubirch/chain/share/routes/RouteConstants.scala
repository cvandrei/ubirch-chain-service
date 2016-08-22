package com.ubirch.chain.share.routes

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
  val block = "block"
  val fullBlock = "fullBlock"

  val urlPrefix = s"/$api/$v1/$chainService"
  val urlHash = s"$urlPrefix/$hash"
  val urlExplorerPrefix = s"$urlPrefix/$explorer"
  val urlExplorerHashPrefix = s"$urlExplorerPrefix/$hash"
  def urlExplorerHash(hash: String) = s"$urlExplorerHashPrefix/$hash"
  val urlExplorerBlockPrefix = s"$urlExplorerPrefix/$block"
  def urlExplorerBlock(hash: String) = s"$urlExplorerBlockPrefix/$hash"
  val urlExplorerFullBlockPrefix = s"$urlExplorerPrefix/$fullBlock"
  def urlExplorerFullBlock(hash: String) = s"$urlExplorerFullBlockPrefix/$hash"

}
