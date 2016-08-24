package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.core.server.routes.ChainExplorerRouteUtil
import com.ubirch.chain.core.server.util.RouteConstants
import com.ubirch.util.json.MyJsonProtocol
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * author: cvandrei
  * since: 2016-07-28
  */
trait ChainExplorerRoute extends MyJsonProtocol {

  private val empty404 = HttpResponse(StatusCodes.NotFound)

  private val chainExplorerRouteUtil = new ChainExplorerRouteUtil

  val route: Route = {

    (get & pathPrefix(RouteConstants.explorer)) {

      pathPrefix(RouteConstants.eventHash / Segment) { hash =>
        complete {
          chainExplorerRouteUtil.eventHash(hash) map {
            case None => empty404
            case Some(hashInfo) => hashInfo
          }
        }

      } ~ path(RouteConstants.blockInfo / Segment) { hash =>
        complete {
          chainExplorerRouteUtil.blockInfo(hash) map {
            case None => empty404
            case Some(blockInfo) => blockInfo
          }
        }

      } ~ path(RouteConstants.fullBlock / Segment) { hash =>
        complete {
          chainExplorerRouteUtil.fullBlock(hash) map {
            case None => empty404
            case Some(fullBlock) => fullBlock
          }
        }

      }

    }

  }

}
