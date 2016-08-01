package com.ubirch.chain.backend.util

import org.scalatest.{Matchers, FeatureSpec}

/**
  * author: cvandrei
  * since: 2016-08-01
  */
class HashUtilSpec extends FeatureSpec
  with Matchers {

  feature("HashUtil.hexString") {

    scenario("ensure that SHA-256 is configured") {
      HashUtil.hexString("ubirchChainService") should be("8faa746945edc3313ae873802616fe17e79f13623aac81e38cbde9e700c33b92")
    }

  }

  feature("HashUtil.bytesToHex && HashUtil.stringToBytes") {

    scenario("calculate hexString, convert to byte array and convert byte array back to hexString") {

      val input = "ubirchChainService"
      val expected = HashUtil.hexString(input)
      println(s"expected: $expected")

      // test part 1: stringToBytes
      val expectedBytes = HashUtil.convertToBytes(expected)
      expectedBytes.length should be(32)

      // test part 2: bytesToHex
      val actual = HashUtil.convertToHex(expectedBytes)
      println(s"actual: $actual")
      actual should be(expected)

    }

  }

  feature("HashUtil.bytesToHex && HashUtil.byteArray") {

    scenario("calculate hash as byte array and convert back to hexString") {

      val input = "ubirchChainService"
      val expected = HashUtil.hexString(input)
      println(s"expected: $expected")

      // test part 1: byteArray
      val expectedBytes = HashUtil.byteArray(input)
      expectedBytes.length should be(32)

      // test part 2: bytesToHex
      val actual = HashUtil.convertToHex(expectedBytes)
      println(s"actual: $actual")
      actual should be(expected)

    }

  }

}
