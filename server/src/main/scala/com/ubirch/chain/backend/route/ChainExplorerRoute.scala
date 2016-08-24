package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.core.server.routes.ChainExplorerRouteUtil
import com.ubirch.chain.core.server.util.RouteConstants
import com.ubirch.util.json.MyJsonProtocol
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-28
  */
trait ChainExplorerRoute extends MyJsonProtocol {

  private val chainExplorerRouteUtil = new ChainExplorerRouteUtil

  val route: Route = {

    (get & pathPrefix(RouteConstants.explorer)) {

      pathPrefix(RouteConstants.eventHash / Segment) { hash =>
        onSuccess(chainExplorerRouteUtil.eventHash(hash)) {
          case None => complete(NotFound)
          case Some(hashInfo) => complete(hashInfo)
        }
      }

    } ~ path(RouteConstants.blockInfo / Segment) { hash =>
      onSuccess(chainExplorerRouteUtil.blockInfo(hash)) {
        case None => complete(NotFound)
        case Some(blockInfo) => complete(blockInfo)
      }

    } ~ path(RouteConstants.fullBlock / Segment) { hash =>
      onSuccess(chainExplorerRouteUtil.fullBlock(hash)) {
        case None => complete(NotFound)
        case Some(fullBlock) => complete(fullBlock)
      }
    }

  }

}
