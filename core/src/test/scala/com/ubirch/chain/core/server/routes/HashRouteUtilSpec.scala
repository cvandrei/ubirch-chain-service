package com.ubirch.chain.core.server.routes

import com.ubirch.chain.json.{Data, Hash}
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.crypto.hash.HashUtil
import org.scalatest.{FeatureSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * author: cvandrei
  * since: 2016-08-17
  */
class HashRouteUtilSpec extends FeatureSpec
  with Matchers {

  private val hashRouteUtil = new HashRouteUtil

  // TODO add before() phase to reset database

  feature("HashRouteUtil.hash") {

    scenario("invalid input: empty string") {
      hashRouteUtil.hash(Data("")).map(_ shouldBe None)
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

      ChainStorageServiceClient.unminedHashes() map { unmined =>
        val hashes = unmined.hashes
        hashes should have length 1
        hashes should contain(expectedHash)
      }
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

      ChainStorageServiceClient.unminedHashes() map { unmined =>

        val hashes = unmined.hashes
        hashes should have length 2
        hashes.head should be(expectedHash)
        hashes(1) should be(expectedHash)

      }

    }

  }

}
