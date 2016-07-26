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

    ignore("POST data (anything that can be represented as a string) and an external id") {
      info("chainService responds with: (sha256(data), timestamp, externalId)")
    }

  }

  feature("chain explorer") {

    info("here we need the txHash result from notifying chainService about a new transaction")

    ignore("GET txHash") {
      info("chainService responds with: (txInfo(txHash, externalId, timeCreated), blockInfo(blockId, blockHash, timeCreated))")
    }

    ignore("GET externalId") {
      info("chainService responds with: (txInfo(txHash, externalId, timeCreated), blockInfo(blockId, blockHash, timeCreated))")
    }

  }

}
