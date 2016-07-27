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

    ignore("POST /api/v1/chainService/hash -- data: envelope(dataString, externalId)") {
      info("chainService responds with: (sha256(data), timestamp, externalId)")
    }

  }

  feature("chain explorer") {

    info("here we need the txHash result from notifying chainService about a new transaction")

    ignore("GET /api/v1/chainService/explore/hash/:hash") {
      info("chainService responds with: (txInfo(hash, externalId, timeCreated), blockInfo(blockId, blockHash, timeCreated))")
    }

    ignore("GET /api/v1/chainService/explore/externalId/:externalId") {
      info("chainService responds with: (txInfo(hash, externalId, timeCreated), blockInfo(blockId, blockHash, timeCreated))")
    }

    ignore("GET /api/v1/chainService/explore/block/:hash") {
      info("chainService responds with: block tree from notes")
    }

  }

  feature("query health") {

    ignore("GET /") {
      info("responds with: {\"version\":\"1.0\",\"status\":\"OK\",\"message\":\"Welcome to the ubirchChainServer\"}")
    }
  }

}
