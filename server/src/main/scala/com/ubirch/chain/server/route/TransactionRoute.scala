package com.ubirch.chain.server.route

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.chain.config.ChainConfig
import com.ubirch.chain.core.actor.{ActorNames, BigchainActor}
import com.ubirch.chain.model.rest.Transaction
import com.ubirch.chain.util.server.RouteConstants
import com.ubirch.util.http.response.ResponseUtil
import com.ubirch.util.rest.akka.directives.CORSDirective

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.routing.RoundRobinPool
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-06-23
  */
trait TransactionRoute extends CORSDirective
  with ResponseUtil
  with StrictLogging {

  implicit val system = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout = Timeout(ChainConfig.actorTimeout seconds)

  private val bigchainActor = system.actorOf(new RoundRobinPool(ChainConfig.akkaNumberOfWorkers).props(Props(new BigchainActor())), ActorNames.BIGCHAIN)

  val route: Route = {

    path(RouteConstants.tx) {
      respondWithCORS {

        post {
          entity(as[Transaction]) { transaction =>
            bigchainActor ! transaction
            complete(StatusCodes.OK)
          }
        }

      }
    }

  }

}
