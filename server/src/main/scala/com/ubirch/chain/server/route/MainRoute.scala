package com.ubirch.chain.server.route

import com.ubirch.chain.util.server.RouteConstants
import com.ubirch.util.mongo.connection.MongoUtil

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

/**
  * author: cvandrei
  * since: 2017-03-22
  */
class MainRoute()(implicit _system: ActorSystem, mongoBigchain: MongoUtil, mongoChain: MongoUtil) {

  val welcome = new WelcomeRoute {}
  val deepCheck = new DeepCheckRoute()(_system = _system, mongoBigchain = mongoBigchain, mongoChain = mongoChain)
  val tx = new TransactionRoute {}

  val myRoute: Route = {

    pathPrefix(RouteConstants.apiPrefix) {
      pathPrefix(RouteConstants.serviceName) {
        pathPrefix(RouteConstants.currentVersion) {

          pathEndOrSingleSlash {
            welcome.route
          } ~ path(RouteConstants.check) {
            welcome.route
          } ~
            deepCheck.route ~
            tx.route

        }
      }
    } ~
      pathSingleSlash {
        welcome.route
      }

  }

}
