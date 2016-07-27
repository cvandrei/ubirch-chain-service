package com.ubirch.chain.backend.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.json.{MyJsonProtocol, WelcomeMessage}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import org.json4s.jackson.JsonMethods.{parse => _}
import org.json4s.jackson.Serialization.{write => _}

/**
  * author: cvandrei
  * since: 2016-07-27
  */
class WelcomeRoute extends MyJsonProtocol {

  val route: Route = {

    get {
      complete {
        WelcomeMessage(message = "Welcome to the ubirchChainServer")
      }
    }
  }
}


