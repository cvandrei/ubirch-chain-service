package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.util.RouteConstants
import com.ubirch.chain.json.util.MyJsonProtocol
import com.ubirch.chain.storage.ChainStorage
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-28
  */
trait ChainExplorerRoute extends MyJsonProtocol with ChainStorage {

  private val empty404 = HttpResponse(StatusCodes.NotFound)

  val route: Route = {

    pathPrefix(RouteConstants.explorer) {

      pathPrefix(RouteConstants.hash / Segment) { hash =>

        get {
          complete {
            getHash(hash) match {
              case None => empty404
              case Some(blockInfo) => blockInfo
            }
          }
        }

      } ~ path(RouteConstants.block / Segment) { hash =>

        get {
          complete {
            getBlock(hash) match {
              case None => empty404
              case Some(blockInfo) => blockInfo
            }
          }

        }

      } ~ path(RouteConstants.fullBlock / Segment) { hash =>

        get {
          complete {
            getBlockWithHashes(hash) match {
              case None => empty404
              case Some(blockInfo) => blockInfo
            }
          }
        }

      }

    }

  }

}
