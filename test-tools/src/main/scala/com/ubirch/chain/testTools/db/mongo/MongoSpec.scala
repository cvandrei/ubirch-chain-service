package com.ubirch.chain.testTools.db.mongo

import com.ubirch.chain.config.ChainConfigKeys
import com.ubirch.util.mongo.connection.MongoUtil
import com.ubirch.util.mongo.test.MongoTestUtils

import org.scalatest.{AsyncFeatureSpec, BeforeAndAfterAll, BeforeAndAfterEach, Matchers}

/**
  * author: cvandrei
  * since: 2017-06-30
  */
trait MongoSpec extends AsyncFeatureSpec
  with Matchers
  with BeforeAndAfterEach
  with BeforeAndAfterAll {

  protected implicit val mongoChain: MongoUtil = new MongoUtil(ChainConfigKeys.MONGO_CHAIN_SERVICE_PREFIX)

  protected val mongoTestUtils = new MongoTestUtils()

  override protected def beforeEach(): Unit = {
    mongoChain.db map (_.drop())
    Thread.sleep(100)
  }

  override protected def afterAll(): Unit = mongoChain.close()

}
