package com.ubirch.backend.services.chain.DummyTest

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

/**
  * Created by derMicha on 25/07/16.
  */
class DummyTest extends FeatureSpec
  with Matchers
  with GivenWhenThen
  with ScalaFutures
  with LazyLogging {

  feature("this is a dummy test") {

    info("we test nothing")

    ignore("this tests feature 1") {
    }

    scenario("this tests feature 2") {
      logger.info("some log infos")
      1 shouldBe 1
    }
  }
}
