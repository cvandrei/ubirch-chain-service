package com.ubirch.chain.backend.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.json.{MyJsonProtocol, WelcomeMessage}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait WelcomeRoute extends MyJsonProtocol {

  val route: Route = {

    get {
      complete {
        WelcomeMessage(message = "Welcome to the ubirchChainServer")
      }
    }
  }
}


