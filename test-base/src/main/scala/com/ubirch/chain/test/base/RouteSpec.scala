package com.ubirch.chain.test.base

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.ubirch.util.json.MyJsonProtocol

/**
  * author: cvandrei
  * since: 2016-08-22
  */
trait RouteSpec extends ElasticSearchSpec
  with ScalatestRouteTest
  with MyJsonProtocol {

}
