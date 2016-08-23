package com.ubirch.chain.core.server.routes

import com.ubirch.backend.chain.model.Welcome
import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2016-08-17
  */
class WelcomeRouteUtilSpec extends FeatureSpec
  with Matchers {

  feature("WelcomeRouteUtil.welcome") {
    scenario("returns expected message") {
      WelcomeRouteUtil.welcome should be(Welcome(message = "Welcome to the ubirchChainServer"))
    }
  }

}
