package com.ubirch.chain.util.server

/**
  * author: cvandrei
  * since: 2017-03-22
  */
object RouteConstants {

  final val apiPrefix = "api"
  final val serviceName = "chainService"
  final val currentVersion = "v1"

  final val check = "check"
  final val deepCheck = "deepCheck"

  final val tx = "tx"
  final val deviceData = "deviceData"
  final val deviceDataHash = "deviceDataHash"

  val pathPrefix = s"/$apiPrefix/$serviceName/$currentVersion"

  val pathCheck = s"$pathPrefix/$check"
  val pathDeepCheck = s"$pathPrefix/$deepCheck"

  val pathTxPrefix = s"$pathPrefix/$tx"
  val pathTxDeviceData = s"$pathTxPrefix/$deviceData"
  val pathTxDeviceDataHash = s"$pathTxPrefix/$deviceDataHash"

}
