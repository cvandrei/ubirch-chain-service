package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.util.MyJsonProtocol
import com.ubirch.chain.hash.HashUtil
import com.ubirch.chain.json.{Hash, Data}
import com.ubirch.chain.share.RouteConstants
import com.ubirch.chain.storage.ChainStorage
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait HashRoute extends MyJsonProtocol with ChainStorage {

  private val invalidData: Set[String] = Set("")

  val route: Route = {

    path(RouteConstants.hash) {
      post {
        entity(as[Data]) { input =>
          complete {

            invalidData.contains(input.data) match {

              case true => BadRequest

              case false =>
                val hash = HashUtil.sha256HexString(input.data)
                storeHash(hash)
                Hash(hash)

            }

          }
        }
      }
    }

  }

}
