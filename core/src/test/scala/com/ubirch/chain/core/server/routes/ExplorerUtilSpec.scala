package com.ubirch.chain.core.server.routes

import com.ubirch.backend.chain.model.HashRequest
import com.ubirch.chain.share.util.HashRouteUtil
import com.ubirch.chain.test.base.ElasticSearchSpec
import com.ubirch.chain.test.util.BlockGenerator

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2016-08-22
  */
class ExplorerUtilSpec extends ElasticSearchSpec {

  private val explorerUtil = new ExplorerUtil
  private val hashRouteUtil = new HashRouteUtil
  private val timeout = 30 seconds
  private val timeoutFullTest = 180 seconds

  feature("ChainExplorerRouteUtil.eventHash") {

    scenario("query unknown hash") {

      Await.result(for {
        res <- explorerUtil.eventHash("1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff")
      } yield {
        res should be(None)
      }, timeoutFullTest)

    }

    scenario("query known hash") {

      // prepare
      val input = HashRequest("""{"foo": {"bar": 42}}""")
      Await.result(hashRouteUtil.hash(input) map {

        case None => fail("failed to create hash during preparation")

        case Some(hashResponse) =>

          Thread.sleep(500)
          val hash = hashResponse.hash

          for {
            res <- explorerUtil.eventHash(hash) // test
          } yield {

            // verify
            res shouldNot be(None)
            res.get.hash shouldBe hash

          }

      }, timeoutFullTest)

    }

  }

  feature("ChainExplorerRouteUtil.blockInfo") {

    scenario("query unknown hash") {

      Await.result(for {
        res <- explorerUtil.blockInfo("1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff") // test
      } yield {

        // verify
        res should be(None)

      }, timeoutFullTest)

    }

    scenario("query known block hash") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeout)
      val minedBlock = Await.result(BlockGenerator.generateMinedBlock(), timeout)

      Await.result(for {
        block <- explorerUtil.blockInfo(minedBlock.hash) // test
      } yield {

        // verify
        block shouldNot be(None)
        block.get.hash shouldEqual minedBlock.hash

      }, timeoutFullTest)

    }

  }

  feature("ChainExplorerRouteUtil.blockInfoByPrevious") {

    scenario("query unknown hash") {

      // test
      Await.result(for {
        res <- explorerUtil.blockInfoByPreviousBlockHash("1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff")
      } yield {

        // verify
        res should be(None)

      }, timeoutFullTest)

    }

    scenario("query known block hash") {

      // prepare
      val genesis = Await.result(BlockGenerator.createGenesisBlock(), timeout)
      val fullBlock = Await.result(BlockGenerator.generateMinedBlock(), timeout)

      Await.result(for {
        blockOpt <- explorerUtil.blockInfoByPreviousBlockHash(genesis.hash) // test
      } yield {

        // verify
        blockOpt shouldBe 'isDefined
        val block = blockOpt.get
        block.hash shouldEqual fullBlock.hash
        block.previousBlockHash shouldEqual fullBlock.previousBlockHash

      }, timeoutFullTest)

    }

  }

  feature("ChainExplorerRouteUtil.fullBlock") {

    scenario("query unknown hash") {

      Await.result(for {
      // test
        res <- explorerUtil.fullBlock("1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff")
      } yield {

        // verify
        res should be(None)

      }, timeoutFullTest)

    }

    scenario("query known block hash") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeout)
      val minedBlock = Await.result(BlockGenerator.generateMinedBlock(), timeout)
      minedBlock.hashes shouldBe 'isDefined
      minedBlock.hashes.get.isEmpty shouldBe false

      Await.result(
        for {
          blockOpt <- explorerUtil.fullBlock(minedBlock.hash) // test
        } yield {

          // verify
          blockOpt shouldBe 'isDefined
          val block = blockOpt.get

          block.hash shouldEqual minedBlock.hash
          block.hashes shouldBe 'isDefined
          block.anchors shouldBe 'isEmpty
          block.hashes shouldBe 'isDefined
          block.hashes.get shouldEqual minedBlock.hashes.get

        }, timeoutFullTest)

    }

  }

}
