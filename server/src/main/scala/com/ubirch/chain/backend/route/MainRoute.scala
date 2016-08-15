package com.ubirch.chain.backend.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.share.RouteConstants
import RouteConstants._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
class MainRoute {

  val hash = new HashRoute {}

  val chainExplorer = new ChainExplorerRoute {}

  val welcome = new WelcomeRoute {}

  val myRoute: Route = {

    pathPrefix(api) {
      pathPrefix(v1) {
        pathPrefix(chainService) {

          hash.route ~
            chainExplorer.route

        }
      }
    } ~
      pathSingleSlash {
        welcome.route
      }

  }

}
