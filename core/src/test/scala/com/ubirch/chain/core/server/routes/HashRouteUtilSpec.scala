package com.ubirch.chain.core.server.routes

import com.ubirch.chain.core.storage.ChainStorage
import com.ubirch.chain.json.{Data, Hash}
import com.ubirch.util.crypto.hash.HashUtil
import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2016-08-17
  */
class HashRouteUtilSpec extends FeatureSpec
  with Matchers
  with ChainStorage {

  private val hashRouteUtil = new HashRouteUtil

  // TODO add before() phase to reset database

  feature("HashRouteUtil.hash") {

    scenario("invalid input: empty string") {
      hashRouteUtil.hash(Data("")) shouldBe None
    }

    ignore("valid input -> hash is stored") {

      // TODO activate test
      // prepare
      val input = Data("""{"foo": {"bar": 42}}""")

      // test
      val res = hashRouteUtil.hash(input)

      // verify
      val expectedHash = HashUtil.sha256HexString(input.data)
      res shouldBe Some(Hash(expectedHash))

      val unmined = unminedHashes()
      unmined should have length 1
      unmined should contain(expectedHash)
    }

    ignore("send same input twice") {

      // TODO activate test
      // prepare
      val input = Data("""{"foo": {"bar": 42}}""")

      // test: send input: 1st time
      val res1 = hashRouteUtil.hash(input)

      // test: send input: 2nd time
      val res2 = hashRouteUtil.hash(input)

      // verify
      val expectedHash = HashUtil.sha256HexString(input.data)
      res1 shouldBe Some(Hash(expectedHash))
      res2 shouldBe Some(Hash(expectedHash))

      val unmined = unminedHashes()
      unmined should have length 2
      unmined.head should be(expectedHash)
      unmined(1) should be(expectedHash)

    }

  }

}
