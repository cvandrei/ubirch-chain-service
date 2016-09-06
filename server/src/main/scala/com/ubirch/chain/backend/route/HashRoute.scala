package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import com.ubirch.backend.chain.model.HashRequest
import com.ubirch.chain.core.server.util.RouteConstants
import com.ubirch.chain.share.util.HashRouteUtil
import com.ubirch.util.json.MyJsonProtocol
import com.ubirch.util.rest.akka.directives.CORSDirective
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait HashRoute extends MyJsonProtocol
  with CORSDirective {

  private val hashRouteUtil = new HashRouteUtil

  val route: Route = {

    path(RouteConstants.hash) {
      post {
        entity(as[HashRequest]) { input =>
          respondWithCORS {

            onSuccess(hashRouteUtil.hash(input)) {
              case None => complete(BadRequest)
              case Some(hash) => complete(hash)
            }

          }
        }
      }
    }

  }

}
