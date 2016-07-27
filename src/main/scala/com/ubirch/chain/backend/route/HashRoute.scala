package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.util.ChainConstants
import com.ubirch.chain.json.MyJsonProtocol
import akka.http.scaladsl.server.Directives._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait HashRoute extends MyJsonProtocol {

  val route: Route = {

    pathPrefix(ChainConstants.hash) {
      post {
        complete {
          StatusCodes.OK
        }
      }
    }

  }

}
