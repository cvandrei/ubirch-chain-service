package com.ubirch.chain.backend.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.util.{MyJsonProtocol, ChainConstants, HashUtil}
import com.ubirch.chain.json.{HashResponse, Envelope}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import org.joda.time.DateTime
import akka.http.scaladsl.model.StatusCodes._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait HashRoute extends MyJsonProtocol {

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
                HashResponse(hash, DateTime.now)
              // TODO tell storage

            }

          }
        }
      }
    }

  }

}
