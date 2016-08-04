package com.ubirch.chain.notary

import com.ubirch.chain.hash.HashUtil
import com.ubirch.chain.json.Anchor

import scala.util.Random

/**
  * author: cvandrei
  * since: 2016-08-04
  */
object NotaryClient {

  def notarize(blockHash: String): Anchor = {

    // TODO actual implementation
    Anchor("bitcoin", HashUtil.hexString("anotherHash" + Random.nextInt))

  }

}
