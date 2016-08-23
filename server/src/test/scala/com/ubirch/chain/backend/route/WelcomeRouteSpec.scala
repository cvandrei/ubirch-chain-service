package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.ubirch.backend.chain.model.Welcome
import com.ubirch.util.json.MyJsonProtocol
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2016-07-29
  */
class WelcomeRouteSpec extends FeatureSpec
  with Matchers
  with ScalatestRouteTest
  with MyJsonProtocol {

  val routes = (new MainRoute).myRoute

  feature("call health/welcome page") {

    scenario("GET /") {
      Get() ~> routes ~> check {
        status shouldEqual OK
        responseAs[Welcome] shouldEqual Welcome(message = "Welcome to the ubirchChainServer")
      }
    }

    scenario("POST /") {
      Post() ~> Route.seal(routes) ~> check {
        status shouldEqual MethodNotAllowed
      }
    }

  }

}
