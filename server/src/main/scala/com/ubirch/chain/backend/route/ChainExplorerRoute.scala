package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.util.MyJsonProtocol
import com.ubirch.chain.core.storage.ChainStorage
import com.ubirch.chain.json.Hash
import com.ubirch.chain.share.RouteConstants
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-28
  */
trait ChainExplorerRoute extends MyJsonProtocol with ChainStorage {

  private val empty404 = HttpResponse(StatusCodes.NotFound)

  val route: Route = {

    (get & pathPrefix(RouteConstants.explorer)) {

      pathPrefix(RouteConstants.hash / Segment) { hash =>
        complete {
          val hashObject = Hash(hash)
          getHashInfo(hashObject) match {
            case None => empty404
            case Some(hashInfo) => hashInfo
          }
        }

      } ~ path(RouteConstants.block / Segment) { hash =>
        complete {
          val hashObject = Hash(hash)
          getBlock(hashObject) match {
            case None => empty404
            case Some(blockInfo) => blockInfo
          }
        }

      } ~ path(RouteConstants.fullBlock / Segment) { hash =>
        complete {
          val hashObject = Hash(hash)
          getBlockWithHashes(hashObject) match {
            case None => empty404
            case Some(blockInfo) => blockInfo
          }
        }

      }

    }

  }

}
