package com.ubirch.chain.backend.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.util.ChainConstants
import com.ubirch.chain.json.MyJsonProtocol
import com.ubirch.chain.json.hash.{AnchorType, Anchor, BlockInfo, HashInfo}
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
            HashInfo(hash = hash)
            // TODO ask storage
          }
        }

      } ~ path(ChainConstants.block / Segment) { hash =>

        get {
          complete {
            BlockInfo(hash, hash, anchors = Seq(Anchor(AnchorType.bitcoin, hash), Anchor(AnchorType.ubirch, hash)))
            // TODO ask storage
          }
        }

      }

    }

  }

}
