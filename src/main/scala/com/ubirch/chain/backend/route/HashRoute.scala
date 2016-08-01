package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.ChainStorage
import com.ubirch.chain.backend.util.{ChainConstants, HashUtil, MyJsonProtocol}
import com.ubirch.chain.json.Envelope
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait HashRoute extends MyJsonProtocol with ChainStorage {

  private val invalidData: Set[String] = Set("")

  val route: Route = {

    path(ChainConstants.hash) {
      post {
        entity(as[Envelope]) { input =>
          complete {

            invalidData.contains(input.data) match {

              case true => BadRequest

              case false =>
                val hash = HashUtil.hexString(input.data)
                storeHash(hash)

            }

          }
        }
      }
    }

  }

}
