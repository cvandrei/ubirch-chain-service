package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.ubirch.chain.json.WelcomeMessage

import org.json4s.ext.JavaTypesSerializers
import org.json4s.JsonDSL._
import org.json4s.JsonAST._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization._
import org.json4s.native._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
class WelcomeRoute {

  //  import Json4sSupport._

  implicit val serialization = jackson.Serialization
  // or native.Serialization
  implicit val formats = DefaultFormats

  implicit def json4sJacksonFormats: Formats = DefaultFormats ++ JavaTypesSerializers.all

  val route: Route = {
    get {
      complete {
        //StatusCodes.OK
        //        WelcomeMessage(message = "Welcome to the ubirchChainServer")
        write(WelcomeMessage(message = "Welcome to the ubirchChainServer"))
      }
    }
  }

}


