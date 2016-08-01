package com.ubirch.chain.backend.route

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

  val route: Route = {

    pathPrefix(ChainConstants.explorer) {

      pathPrefix(ChainConstants.hash / Segment) { hash =>

        get {
          complete {
            getHash(hash)
          }
        }

      } ~ path(ChainConstants.block / Segment) { hash =>

        get {
          complete {
            getBlock(hash)
          }
        }

      } ~ path(ChainConstants.fullBlock / Segment) { hash =>

        get {
          complete {
            getBlockWithHashes(hash)
          }
        }

      }

    }

  }

}
