package com.ubirch.chain.backend.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.util.ChainConstants
import com.ubirch.chain.json.MyJsonProtocol
import com.ubirch.chain.json.hash.{ExplorerBlock, ExplorerHash}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-28
  */
trait ChainExplorerRoute extends MyJsonProtocol {

  val route: Route = {

    pathPrefix(ChainConstants.explorer) {
      pathPrefix(ChainConstants.hash / Segment) { hash =>

        get {
          complete {
            ExplorerHash(hash)
          }
        }

      } ~ path(ChainConstants.block / Segment) { hash =>

        get {
          complete {
            ExplorerBlock(hash)
          }
        }

      }

    }

  }

}
