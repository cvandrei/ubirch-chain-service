package com.ubirch.chain.backend.service

import org.scalatest.FeatureSpec

/**
  * author: cvandrei
  * since: 2016-07-26
  */
class AcceptanceTestSpec extends FeatureSpec {

  feature("chain explorer") {

    info("here we need the txHash result from notifying chainService about a new transaction")

    ignore("GET /api/v1/chainService/explorer/hash/:hash") {
      info("chainService responds with instance of HashInfo")
    }

    ignore("GET /api/v1/chainService/explorer/block/:hash") {
      info("chainService responds with instance of BlockInfo")
    }

    ignore("GET /api/v1/chainService/explorer/fullBlock/:hash") {
      info("chainService responds with instance of BlockInfo")
    }

  }

}
