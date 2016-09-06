package com.ubirch.chain.share.util

import com.ubirch.backend.chain.model.HashRequest
import com.ubirch.chain.test.base.ElasticSearchSpec
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.crypto.hash.HashUtil

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2016-08-17
  */
class HashRouteUtilSpec extends ElasticSearchSpec {

  private val hashRouteUtil = new HashRouteUtil

  private val timeout = 10 seconds

  feature("HashRouteUtil.hash") {

    scenario("invalid input: empty string") {

      // test
      val hashResponse = Await.result(hashRouteUtil.hash(HashRequest("")), timeout)

      // verify
      hashResponse map (_ shouldBe None)
      Thread.sleep(500)

      val unmined = Await.result(ChainStorageServiceClient.unminedHashes(), timeout)
      unmined.hashes shouldBe 'isEmpty

    }

    scenario("valid input -> hash is stored") {

      // prepare
      val input = HashRequest("""{"foo": {"bar": 42}}""")

      val res = Await.result(hashRouteUtil.hash(input), timeout)
      Thread.sleep(1000)

      // verify
      val expectedHash = HashUtil.sha256HexString(input.data)
      res.get.hash shouldBe expectedHash

      val unmined = Await.result(ChainStorageServiceClient.unminedHashes(), timeout)
      val hashes = unmined.hashes
      hashes should have length 1
      hashes should contain(expectedHash)

    }

  }

  scenario("send same input twice -> same hash stored once") {

    // prepare
    val input = HashRequest("""{"foo": {"bar": 42}}""")

    // test: send input: 1st time
    val res1 = Await.result(hashRouteUtil.hash(input), timeout)
    // test: send input: 2nd time
    val res2 = Await.result(hashRouteUtil.hash(input), timeout)

    // verify
    val expectedHash = HashUtil.sha256HexString(input.data)
    res1.get.hash shouldBe expectedHash
    res2.get.hash shouldBe expectedHash

    Thread.sleep(1000)
    val unmined = Await.result(ChainStorageServiceClient.unminedHashes(), timeout)

    val hashes = unmined.hashes
    hashes should have length 1
    hashes.head should be(expectedHash)

  }

}
