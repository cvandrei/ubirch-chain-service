package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.backend.chain.model.HashRequest
import com.ubirch.chain.core.server.util.RouteConstants
import com.ubirch.chain.share.util.HashRouteUtil
import com.ubirch.util.json.MyJsonProtocol
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait HashRoute extends MyJsonProtocol {

  private val hashRouteUtil = new HashRouteUtil

  val route: Route = {

    path(RouteConstants.hash) {
      post {
        entity(as[HashRequest]) { input =>
          complete {

            hashRouteUtil.hash(input) map {
              case None => BadRequest
              case Some(hash) => hash
            }

          }
        }
      }
    }

  }

}
