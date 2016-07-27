package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

/**
  * author: cvandrei
  * since: 2016-07-27
  */
class MainRoute {

  //  override def actorRefFactory = context

  //  val transaction = new TransactionRoute {
  //    override implicit def actorRefFactory = context
  //  }

  //  val chainExplorer = new ChainExplorerRoute {
  //    override implicit def actorRefFactory = context
  //  }

  val welcome = new WelcomeRoute
  //    override implicit def actorRefFactory = context
  //  }

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
