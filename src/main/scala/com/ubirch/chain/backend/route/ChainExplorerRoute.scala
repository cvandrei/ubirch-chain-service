package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.ChainStorage
import com.ubirch.chain.backend.util.{ChainConstants, MyJsonProtocol}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-28
  */
trait ChainExplorerRoute extends MyJsonProtocol with ChainStorage {

  private val empty404 = HttpResponse(StatusCodes.NotFound)

  val route: Route = {

    pathPrefix(ChainConstants.explorer) {

      pathPrefix(ChainConstants.hash / Segment) { hash =>

        get {
          complete {
            getHash(hash) match {
              case None => empty404
              case Some(blockInfo) => blockInfo
            }
          }
        }

      } ~ path(ChainConstants.block / Segment) { hash =>

        get {
          complete {
            getBlock(hash) match {
              case None => empty404
              case Some(blockInfo) => blockInfo
            }
          }

        }

      } ~ path(ChainConstants.fullBlock / Segment) { hash =>

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
