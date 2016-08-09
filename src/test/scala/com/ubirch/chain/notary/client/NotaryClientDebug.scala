package com.ubirch.chain.notary.client

import java.net.URL

import com.ubirch.chain.hash.HashUtil
import com.ubirch.chain.json.Hash
import com.ubirch.chain.json.util.MyJsonProtocol
import org.json4s.native.JsonMethods._
import org.scalatest.{FeatureSpec, Matchers}
import uk.co.bigbeeconsultants.http.HttpClient
import uk.co.bigbeeconsultants.http.header.MediaType._
import uk.co.bigbeeconsultants.http.request.RequestBody

/**
  * author: cvandrei
  * since: 2016-08-08
  */
class NotaryClientDebug extends FeatureSpec
  with Matchers
  with MyJsonProtocol {

  feature("NotaryClient.notarize") {

    ignore("for manual debug") {

      val event = "ubirch-chain-test-2342"
      val url = new URL("http://localhost:8080/api/v1/chainService/hash")
      val json = s"""{"data": "$event"}"""
      val body = RequestBody(json, APPLICATION_JSON)

      val httpClient = new HttpClient
      val res = httpClient.post(url, Some(body))

      println("=== RESPONSE ===")
      println(s"status=${res.status}, body=${res.body.asString}")
      val hash = parse(res.body.asString).extract[Hash]

      val expected = HashUtil.hexString(event)
      hash.hash should be(expected)

      println(s"hash=${hash.hash}")

    }

  }

}
