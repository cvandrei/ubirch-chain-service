package com.ubirch.chain.backend.service

import akka.actor.Actor
import com.ubirch.chain.backend.util.ChainConstants
import spray.routing.{HttpService, Route}
import spray.http.StatusCodes._

/**
  * author: cvandrei
  * since: 2016-07-26
  */
class ChainServiceActor extends Actor with ChainService {

  def actorRefFactory = context
  def receive = runRoute(routes)

}

trait ChainService extends HttpService {

  val routes: Route =

    pathPrefix(ChainConstants.pathVersion) {
      complete{
        OK
      }
    }

}
