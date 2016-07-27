package com.ubirch.chain.backend.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import de.heikoseeberger.akkahttpjson4s.Json4sSupport

import org.json4s._
import org.json4s.ext.JavaTypesSerializers
import org.json4s.jackson.JsonMethods.{parse => _}
import org.json4s.jackson.Serialization.{write => _}

import com.ubirch.chain.json.WelcomeMessage

/**
  * author: cvandrei
  * since: 2016-07-27
  */
class WelcomeRoute {

  import Json4sSupport._

  implicit val serialization = jackson.Serialization // or native.Serialization

  implicit def json4sJacksonFormats: Formats = DefaultFormats ++ JavaTypesSerializers.all

  val route: Route = {

    get {
      complete {
        WelcomeMessage(message = "Welcome to the ubirchChainServer")
      }
    }
  }
}


