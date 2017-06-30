package com.ubirch.chain.core.manager

import com.ubirch.chain.testTools.db.mongo.MongoSpec

/**
  * author: cvandrei
  * since: 2017-06-30
  */
class BlockInfoManagerSpec extends MongoSpec {

  feature("findByBlockHash()") {

    scenario("empty database") {
      BlockInfoManager.findByBlockHash("6670b0e12d04f9464c1a1467220bed6f734a8fd5f7b5cbc570ea5097973b693f") map (_ should be('isEmpty))
    }

    // TODO test case: some records exist but not the one we're looking for

    // TODO test case: blockHash exists

  }

}
