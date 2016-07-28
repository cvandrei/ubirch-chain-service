package com.ubirch.chain.backend.service

import org.scalatest.FeatureSpec

/**
  * author: cvandrei
  * since: 2016-07-26
  */
class AcceptanceTestSpec extends FeatureSpec
  /*  with Matchers // TODO enable when needed*/
  /*  with GivenWhenThen // TODO enable when needed*/
  /*  with ScalaFutures // TODO enable when needed*/
  /*  with LazyLogging // TODO enable when needed */ {

  feature("notify chainService about a new transaction") {

    ignore("POST /api/v1/chainService/hash -- data: envelope(dataString)") {
      info("chainService responds with: (sha256(data), timestamp)")
      // TODO replace with actual test
    }

  }

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

  feature("query health") {

    ignore("GET /") {
      info("responds with: {\"version\":\"1.0\",\"status\":\"OK\",\"message\":\"Welcome to the ubirchChainServer\"}")
      // TODO replace with actual test
    }
  }

}
