package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import com.ubirch.backend.chain.model.HashInfo
import com.ubirch.chain.core.server.routes.HashRouteUtil
import com.ubirch.chain.json.Data
import com.ubirch.chain.share.routes.RouteConstants
import com.ubirch.chain.util.test.RouteSpec
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-08-22
  */
class ChainExplorerRouteSpec extends RouteSpec {

  private val routes = (new MainRoute).myRoute
  private val hashRouteUtil = new HashRouteUtil

  feature(s"call hash explorer route: ${RouteConstants.urlExplorerHashPrefix}/:hash") {

    scenario("GET without hash in address") {
      Get(RouteConstants.urlExplorerHash("")) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET unknown hash") {
      val hash = "1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff"
      Get(RouteConstants.urlExplorerHash(hash)) ~> routes ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET known hash") {

      // prepare
      val data = Data("""{"foo": {"bar": 42}}""")
      for {
        hashRes <- hashRouteUtil.hash(data)
      } yield {

        hashRes shouldNot be(None)
        val hash = hashRes.get.hash
        Thread.sleep(500)

        // test
        Get(RouteConstants.urlExplorerHash(hash)) ~> routes ~> check {
          status shouldEqual OK
          responseAs[HashInfo].hash shouldEqual hash
        }
      }

    }

  }

  feature(s"call hash explorer route: ${RouteConstants.urlExplorerBlockPrefix}/:hash") {

    scenario("GET without hash in address") {
      Get(RouteConstants.urlExplorerBlock("")) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET unknown hash") {
      val hash = "1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff"
      Get(RouteConstants.urlExplorerBlock(hash)) ~> routes ~> check {
        status shouldEqual NotFound
      }
    }

    ignore("GET known hash") {
      // TODO write test
    }

  }

  feature(s"call hash explorer route: ${RouteConstants.urlExplorerFullBlockPrefix}/:hash") {

    scenario("GET without hash in address") {
      Get(RouteConstants.urlExplorerFullBlock("")) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET unknown hash") {
      val hash = "1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff"
      Get(RouteConstants.urlExplorerFullBlock(hash)) ~> routes ~> check {
        status shouldEqual NotFound
      }
    }

    ignore("GET known hash") {
      // TODO write test
    }

  }

}
