package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
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

  private val unknownEventHash = HttpResponse(NotFound, entity = HttpEntity("hash is unknown or has not been mined yet"))
  private val unknownBlockHash = HttpResponse(NotFound, entity = HttpEntity("hash is unknown"))

  val route: Route = {

    (get & pathPrefix(RouteConstants.explorer)) {

      pathPrefix(RouteConstants.eventHash / Segment) { hash =>

        onSuccess(chainExplorerRouteUtil.eventHash(hash)) {
          case None => complete(unknownEventHash)
          case Some(hashInfo) => complete(hashInfo)
        }

      } ~ path(RouteConstants.blockInfo / Segment) { hash =>

        onSuccess(chainExplorerRouteUtil.blockInfo(hash)) {
          case None => complete(unknownBlockHash)
          case Some(blockInfo) => complete(blockInfo)
        }

      } ~ path(RouteConstants.blockInfoByPrevious / Segment) { hash =>

        onSuccess(chainExplorerRouteUtil.blockInfoByPreviousBlockHash(hash)) {
          case None => complete(unknownBlockHash)
          case Some(blockInfo) => complete(blockInfo)
        }

      } ~ path(RouteConstants.fullBlock / Segment) { hash =>

        onSuccess(chainExplorerRouteUtil.fullBlock(hash)) {
          case None => complete(unknownBlockHash)
          case Some(fullBlock) => complete(fullBlock)
        }

      }

    }

  }

}
