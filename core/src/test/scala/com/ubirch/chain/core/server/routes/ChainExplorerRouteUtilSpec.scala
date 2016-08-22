package com.ubirch.chain.core.server.routes

import com.ubirch.chain.json.Data
import com.ubirch.chain.util.test.ElasticSearchSpec

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * author: cvandrei
  * since: 2016-08-22
  */
class ChainExplorerRouteUtilSpec extends ElasticSearchSpec {

  private val chainExplorerUtil = new ChainExplorerRouteUtil
  private val hashRouteUtil = new HashRouteUtil

  feature("ChainExplorerRouteUtil.hash") {

    scenario("query unknown hash") {

      for {
        res <- chainExplorerUtil.hash("1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff")
      } yield {
        res should be(None)
      }

    }


    scenario("query known hash") {

      // prepare
      val input = Data("""{"foo": {"bar": 42}}""")
      hashRouteUtil.hash(input) map {

        case None => fail("failed to create hash during preparation")

        case Some(hashResponse) =>

          Thread.sleep(500)
          val hash = hashResponse.hash

          for {
          // test
            res <- chainExplorerUtil.hash(hash)
          } yield {

            // verify
            res shouldNot be(None)
            res.get.hash shouldBe hash

          }

      }

    }

  }

  feature("ChainExplorerRouteUtil.block") {

    scenario("query unknown hash") {

      for {
      // test
        res <- chainExplorerUtil.block("1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff")
      } yield {

        // verify
        res should be(None)

      }

    }

    ignore("query known block hash") {
      // TODO
    }

  }

  feature("ChainExplorerRouteUtil.fullBlock") {

    scenario("query unknown hash") {

      for {
      // test
        res <- chainExplorerUtil.fullBlock("1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff")
      } yield {

        // verify
        res should be(None)

      }

    }

    ignore("query known block hash") {
      // TODO
    }

  }

}
