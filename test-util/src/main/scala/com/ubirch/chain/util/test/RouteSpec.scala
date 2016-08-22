package com.ubirch.chain.util.test

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.ubirch.backend.util.MyJsonProtocol

/**
  * author: cvandrei
  * since: 2016-08-22
  */
trait RouteSpec extends ElasticSearchSpec
  with ScalatestRouteTest
  with MyJsonProtocol {

}
