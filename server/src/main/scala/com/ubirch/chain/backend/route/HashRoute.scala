package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.util.MyJsonProtocol
import com.ubirch.chain.core.server.routes.HashRouteUtil
import com.ubirch.chain.core.storage.ChainStorage
import com.ubirch.chain.json.Data
import com.ubirch.chain.share.RouteConstants
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait HashRoute extends MyJsonProtocol with ChainStorage {

  private val hashRouteUtil = new HashRouteUtil

  val route: Route = {

    path(RouteConstants.hash) {
      post {
        entity(as[Data]) { input =>
          complete {

            hashRouteUtil.hash(input) match {
              case None => BadRequest
              case Some(hash) => hash
            }

          }
        }
      }
    }

  }

}
