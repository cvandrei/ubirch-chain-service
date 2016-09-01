package com.ubirch.chain.core.server.routes

import com.ubirch.backend.chain.model.HashRequest
import com.ubirch.chain.share.util.{HashRouteUtil, MiningUtil}
import com.ubirch.chain.test.base.ElasticSearchSpec
import com.ubirch.chain.test.util.BlockGenerator

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

      // prepare
        genesis <- BlockGenerator.createGenesisBlock()
        minedBlock <- BlockGenerator.generateMinedBlock()

        // test
        block <- chainExplorerUtil.blockInfo(minedBlock.hash)

      } yield {

        block shouldNot be(None)
        block.get.hash shouldEqual minedBlock.hash

      }
    }

  }

  feature("ChainExplorerRouteUtil.blockInfoByPrevious") {

    scenario("query unknown hash") {

      // test
      for {
        res <- chainExplorerUtil.blockInfoByPreviousBlockHash("1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff")
      } yield {

        // verify
        res should be(None)

      }

    }

    scenario("query known block hash") {

      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock()
        fullBlock <- BlockGenerator.generateMinedBlock()

        // test
        block <- chainExplorerUtil.blockInfoByPreviousBlockHash(fullBlock.previousBlockHash)

      } yield {

        block shouldNot be(None)
        block.get.hash shouldEqual fullBlock.hash
        block.get.previousBlockHash shouldEqual fullBlock.previousBlockHash

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

      // prepare
        genesis <- BlockGenerator.createGenesisBlock()
        minedBlock <- BlockGenerator.generateMinedBlock()

        // test
        block <- chainExplorerUtil.fullBlock(minedBlock.hash)

      } yield {

        block shouldNot be(None)
        block.get.hash shouldEqual minedBlock.hash

      }

    }

  }

}
