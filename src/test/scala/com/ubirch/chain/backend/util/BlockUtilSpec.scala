package com.ubirch.chain.backend.util

import com.roundeights.hasher.Hasher
import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2016-07-29
  */
class BlockUtilSpec extends FeatureSpec
  with Matchers {

  feature("BlockUtil.size") {

    scenario("empty sequence") {

      BlockUtil.size(Seq.empty) shouldEqual 0

    }

    scenario("sequence with two SHA-256 hashes") {

      val seq = Seq(
        "e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86",
        "5ad35cc03ba460f5d1201197be5faeb4a1914317d2f3abf050174f9247c3b1bb"
      )
      BlockUtil.size(seq) shouldEqual 64

    }

    scenario("sequence with two SHA-512 hashes") {

      val hash = Hasher("foo").sha512.hex
      val hashOfHash = Hasher(hash).sha512.hex
      val seq = Seq(hash, hashOfHash)

      BlockUtil.size(seq) shouldEqual 128

    }

  }

}
