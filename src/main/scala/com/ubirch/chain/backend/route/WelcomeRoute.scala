package com.ubirch.chain.backend.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.backend.util.MyJsonProtocol
import com.ubirch.chain.json.Welcome
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait WelcomeRoute extends MyJsonProtocol {

  val route: Route = {

    get {
      complete {
        Welcome(message = "Welcome to the ubirchChainServer")
      }
    }
  }
}


