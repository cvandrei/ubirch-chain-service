package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.ubirch.chain.backend.util.RouteConstants
import com.ubirch.chain.hash.HashUtil
import com.ubirch.chain.json.util.MyJsonProtocol
import com.ubirch.chain.json.{Data, Hash}
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

  feature(s"call hash method: ${RouteConstants.urlHash}") {

    scenario(s"GET (not allowed)") {
      Get(RouteConstants.urlHash) ~> Route.seal(routes) ~> check {
        status shouldEqual MethodNotAllowed
      }
    }

    scenario(s"POST with valid input") {

      val data = Data("ubirch-test")
      Post(RouteConstants.urlHash, data) ~> routes ~> check {
        status shouldEqual OK
        responseAs[Hash].hash shouldEqual HashUtil.sha256HexString(data.data)
      }
    }

    scenario(s"POST without input") {

      Post(RouteConstants.urlHash) ~> Route.seal(routes) ~> check {

        status shouldEqual BadRequest
        response.entity.toString should include("The request content was malformed")

      }
    }

    scenario(s"POST without invalid input (data is empty string)") {

      val data = Data("")
      Post(RouteConstants.urlHash, data) ~> routes ~> check {
        status shouldEqual BadRequest
      }
    }

  }

}
