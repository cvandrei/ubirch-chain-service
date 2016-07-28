package com.ubirch.chain.backend.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.roundeights.hasher.Hasher
import com.ubirch.chain.backend.util.ChainConstants
import com.ubirch.chain.json.MyJsonProtocol
import com.ubirch.chain.json.hash.{Envelope, HashResponse}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import org.joda.time.DateTime

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait HashRoute extends MyJsonProtocol {

  val route: Route = {

    path(ChainConstants.hash) {
      post {
        entity(as[Envelope]) { input =>
          complete {
            HashResponse(Hasher(input.data).sha512.hex, DateTime.now, input.externalId)
          }
        }
      }
    }

  }

}
