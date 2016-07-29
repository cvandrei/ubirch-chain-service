package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.ubirch.chain.backend.util.{ChainConstants, HashUtil, MyJsonProtocol}
import com.ubirch.chain.json.{Envelope, HashResponse}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2016-07-29
  */
class HashRouteSpec extends FeatureSpec
  with Matchers
  with ScalatestRouteTest
  with MyJsonProtocol {

  val routes = (new MainRoute).myRoute

  feature("call hash method") {

    scenario(s"GET ${ChainConstants.urlHash}") {
      Get(ChainConstants.urlHash) ~> Route.seal(routes) ~> check {
        status === MethodNotAllowed
      }
    }

    scenario(s"POST ${ChainConstants.urlHash}") {

      val data = Envelope("ubirch-test")
      Post(ChainConstants.urlHash, data) ~> routes ~> check {
        status === OK
        responseAs[HashResponse].hash shouldEqual HashUtil.hexString(data.data)
      }
    }

  }

}
