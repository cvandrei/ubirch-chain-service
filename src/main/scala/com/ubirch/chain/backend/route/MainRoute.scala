package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

/**
  * author: cvandrei
  * since: 2016-07-27
  */
class MainRoute {

  //  val transaction = new TransactionRoute {}

  //  val chainExplorer = new ChainExplorerRoute {}

  val welcome = new WelcomeRoute {}

  val myRoute: Route = {

    path("api") {
      pathPrefix("v1") {
        pathPrefix("chainService") {
          get {
            complete(StatusCodes.OK)
          }
          //          transaction.route ~
          //            chainExplorer.route
        }
      }
    } ~
      pathSingleSlash {
        welcome.route
      }

  }

}
