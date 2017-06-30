package com.ubirch.chain.server.route

import com.ubirch.chain.config.ChainConfig
import com.ubirch.util.json.MyJsonProtocol
import com.ubirch.util.model.JsonResponse

import akka.http.scaladsl.server.Directives.{complete, get}
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2017-03-22
  */
trait WelcomeRoute extends MyJsonProtocol {

  val route: Route = {

    get {
      complete {

        val goInfo = s"${ChainConfig.goPipelineName} / ${ChainConfig.goPipelineLabel} / ${ChainConfig.goPipelineRevision}"
        JsonResponse(message = s"Welcome to the ubirchChainService ( $goInfo )")

      }
    }

  }

}
