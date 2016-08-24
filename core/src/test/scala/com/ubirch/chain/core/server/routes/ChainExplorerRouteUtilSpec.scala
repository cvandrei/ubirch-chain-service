package com.ubirch.chain.core.server.routes

import com.ubirch.backend.chain.model.HashRequest
import com.ubirch.chain.share.util.{HashRouteUtil, MiningUtil}
import com.ubirch.chain.test.base.ElasticSearchSpec
import com.ubirch.chain.test.util.block.BlockGenerator

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * author: cvandrei
  * since: 2016-08-22
  */
class ChainExplorerRouteUtilSpec extends ElasticSearchSpec {

  private val chainExplorerUtil = new ChainExplorerRouteUtil
  private val hashRouteUtil = new HashRouteUtil
  private val miningUtil = new MiningUtil

  feature("ChainExplorerRouteUtil.eventHash") {

    scenario("query unknown hash") {

      for {
        res <- chainExplorerUtil.eventHash("1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff")
      } yield {
        res should be(None)
      }

    }

    scenario("query known hash") {

      // prepare
      val input = HashRequest("""{"foo": {"bar": 42}}""")
      hashRouteUtil.hash(input) map {

        case None => fail("failed to create hash during preparation")

        case Some(hashResponse) =>

          Thread.sleep(500)
          val hash = hashResponse.hash

          for {
          // test
            res <- chainExplorerUtil.eventHash(hash)
          } yield {

            // verify
            res shouldNot be(None)
            res.get.hash shouldBe hash

          }

      }

    }

  }

  feature("ChainExplorerRouteUtil.blockInfo") {

    scenario("query unknown hash") {

      for {
      // test
        res <- chainExplorerUtil.blockInfo("1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff")
      } yield {

        // verify
        res should be(None)

      }

    }

    scenario("query known block hash") {

      for {
        block <- BlockGenerator.generateMinedBlock(5000)
      } yield {

        val hash = block.hash
        for {
          block <- chainExplorerUtil.blockInfo(hash)
        } yield {

          block shouldNot be(None)
          block.get.hash shouldEqual hash

        }
      }

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

    scenario("query known block hash") {

      for {
        block <- BlockGenerator.generateMinedBlock(5000)
      } yield {

        val hash = block.hash
        for {
          block <- chainExplorerUtil.fullBlock(hash)
        } yield {

          block shouldNot be(None)
          block.get.hash shouldEqual hash

        }
      }

    }

  }

}
