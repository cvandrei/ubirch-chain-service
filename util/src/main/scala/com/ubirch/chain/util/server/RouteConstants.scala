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
  final val deviceMsg = "deviceMsg"
  final val deviceMsgHash = "deviceMsgHash"

  val pathPrefix = s"/$apiPrefix/$serviceName/$currentVersion"

  val pathCheck = s"$pathPrefix/$check"
  val pathDeepCheck = s"$pathPrefix/$deepCheck"

  val pathTxPrefix = s"$pathPrefix/$tx"
  val pathTxDeviceData = s"$pathTxPrefix/$deviceMsg"
  val pathTxDeviceDataHash = s"$pathTxPrefix/$deviceMsgHash"

}
