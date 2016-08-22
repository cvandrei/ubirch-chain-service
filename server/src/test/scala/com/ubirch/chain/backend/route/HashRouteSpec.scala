package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.json.{Data, Hash}
import com.ubirch.chain.share.routes.RouteConstants
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

    scenario(s"POST invalid input (data is empty string)") {

      val data = Data("")
      Post(RouteConstants.urlHash, data) ~> routes ~> check {
        status shouldEqual BadRequest
      }
    }

  }

}
