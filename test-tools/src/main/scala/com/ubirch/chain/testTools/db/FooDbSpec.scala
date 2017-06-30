package com.ubirch.chain.testTools.db

import org.scalatest.{AsyncFeatureSpec, BeforeAndAfterAll, BeforeAndAfterEach, Matchers}

/**
  * author: cvandrei
  * since: 2017-06-23
  */
class FooDbSpec extends AsyncFeatureSpec
  with Matchers
  with BeforeAndAfterEach
  with BeforeAndAfterAll {

  override protected def beforeEach(): Unit = {
    // TODO drop db content
    Thread.sleep(100)
  }

  override protected def afterAll(): Unit = {
    // TODO close db connection
  }

}
