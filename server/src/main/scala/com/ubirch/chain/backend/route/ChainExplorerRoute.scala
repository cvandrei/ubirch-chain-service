package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.util.MyJsonProtocol
import com.ubirch.chain.core.server.routes.ChainExplorerRouteUtil
import com.ubirch.chain.share.RouteConstants
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-28
  */
trait ChainExplorerRoute extends MyJsonProtocol {

  private val empty404 = HttpResponse(StatusCodes.NotFound)

  private val chainExplorerRouteUtil = new ChainExplorerRouteUtil

  val route: Route = {

    (get & pathPrefix(RouteConstants.explorer)) {

      pathPrefix(RouteConstants.hash / Segment) { hash =>
        complete {
          chainExplorerRouteUtil.hash(hash) match {
            case None => empty404
            case Some(hashInfo) => hashInfo
          }
        }

      } ~ path(RouteConstants.block / Segment) { hash =>
        complete {
          chainExplorerRouteUtil.block(hash) match {
            case None => empty404
            case Some(blockInfo) => blockInfo
          }
        }

      } ~ path(RouteConstants.fullBlock / Segment) { hash =>
        complete {
          chainExplorerRouteUtil.fullBlock(hash) match {
            case None => empty404
            case Some(blockInfo) => blockInfo
          }
        }

      }

    }

  }

}
