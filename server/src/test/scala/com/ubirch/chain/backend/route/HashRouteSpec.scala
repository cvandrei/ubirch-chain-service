package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import com.ubirch.backend.chain.model.{HashedData, HashRequest}
import com.ubirch.chain.core.server.util.RouteConstants
import com.ubirch.chain.test.base.RouteSpec
import com.ubirch.util.crypto.hash.HashUtil
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-29
  */
class HashRouteSpec extends RouteSpec {

  val routes = (new MainRoute).myRoute

  feature(s"call hash method: ${RouteConstants.urlHash}") {

    scenario(s"GET (not allowed)") {
      Get(RouteConstants.urlHash) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario(s"POST with valid input") {

      val data = HashRequest("ubirch-test")
      Post(RouteConstants.urlHash, data) ~> routes ~> check {
        status shouldEqual OK
        responseAs[HashedData].hash shouldEqual HashUtil.sha256HexString(data.data)
      }
    }

    scenario(s"POST without input") {

      Post(RouteConstants.urlHash) ~> Route.seal(routes) ~> check {

        status shouldEqual BadRequest
        response.entity.toString should include("The request content was malformed")

      }
    }

    scenario(s"POST invalid input (data is empty string)") {

      val data = HashRequest("")
      Post(RouteConstants.urlHash, data) ~> routes ~> check {
        status shouldEqual BadRequest
      }
    }

  }

}
