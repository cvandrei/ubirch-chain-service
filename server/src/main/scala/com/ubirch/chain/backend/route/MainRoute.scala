package com.ubirch.chain.backend.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.core.server.util.RouteConstants._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
class MainRoute {

  val hash = new HashRoute {}

  val explorer = new ExplorerRoute {}

  val welcome = new WelcomeRoute {}

  val myRoute: Route = {

    pathPrefix(api) {
      pathPrefix(v1) {
        pathPrefix(chainService) {

          hash.route ~
            explorer.route

        }
      }
    } ~
      pathSingleSlash {
        welcome.route
      }

  }

}
