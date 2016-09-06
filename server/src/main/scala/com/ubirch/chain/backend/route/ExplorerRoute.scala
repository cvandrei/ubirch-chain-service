package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Route
import com.ubirch.chain.core.server.routes.ExplorerUtil
import com.ubirch.chain.core.server.util.RouteConstants
import com.ubirch.util.json.MyJsonProtocol
import com.ubirch.util.rest.akka.directives.CORSDirective
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-28
  */
trait ExplorerRoute extends MyJsonProtocol
  with CORSDirective {

  private val explorerUtil = new ExplorerUtil

  private val unknownEventHash = HttpResponse(NotFound, entity = HttpEntity("hash is unknown or has not been mined yet"))
  private val unknownBlockHash = HttpResponse(NotFound, entity = HttpEntity("hash is unknown"))

  val route: Route = {

    respondWithCORS {

      (get & pathPrefix(RouteConstants.explorer)) {

        pathPrefix(RouteConstants.eventHash / Segment) { hash =>

          onSuccess(explorerUtil.eventHash(hash)) {
            case None => complete(unknownEventHash)
            case Some(hashInfo) => complete(hashInfo)
          }

        } ~ path(RouteConstants.blockInfo / Segment) { hash =>

          onSuccess(explorerUtil.blockInfo(hash)) {
            case None => complete(unknownBlockHash)
            case Some(blockInfo) => complete(blockInfo)
          }

        } ~ path(RouteConstants.nextBlockInfo / Segment) { hash =>

          onSuccess(explorerUtil.nextBlockInfo(hash)) {
            case None => complete(unknownBlockHash)
            case Some(blockInfo) => complete(blockInfo)
          }

        } ~ path(RouteConstants.fullBlock / Segment) { hash =>

          onSuccess(explorerUtil.fullBlock(hash)) {
            case None => complete(unknownBlockHash)
            case Some(fullBlock) => complete(fullBlock)
          }

        }

      }

    }

  }

}
