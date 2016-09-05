package com.ubirch.chain.share.util

import com.ubirch.chain.config.Config
import com.ubirch.chain.share.testutil.{BlockGenerator, HashGenerator}
import com.ubirch.chain.test.base.ElasticSearchSpec
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.date.DateUtil

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2016-08-30
  */
class MiningUtilSpec extends ElasticSearchSpec {

  private val timeout = 60 seconds
  private val miningUtil = new MiningUtil

  feature("MiningUtil.blockCheck") {

    scenario("trigger = false") {

      Await.result(
      for {
        mostRecent <- miningUtil.mostRecentBlock()
        blockCheck <- miningUtil.blockCheck() // test
      } yield {

        mostRecent shouldBe None // verify preparation
        blockCheck shouldBe None // verify

      }
        , timeout)

    }

    scenario("trigger = true") {

      Await.result(
      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock()
        minedHashesSize <- HashGenerator.createUnminedHashesFuture(sizeCheckResultsInTrue = true)
        mostRecent <- miningUtil.mostRecentBlock()

        // test
        blockCheck <- miningUtil.blockCheck()

      } yield {

        // verify preparation
        mostRecent.get.hash shouldBe genesis.hash

        // verify
        blockCheck shouldBe Some
        blockCheck.get.previousBlockHash shouldBe genesis.hash

      }
        , timeout)

    }

  }

  feature("MiningUtil.mine") {

    scenario("most recent block does not exist") {

      Await.result(
      for {
        mostRecent <- miningUtil.mostRecentBlock() // prepare
        minedBlock <- miningUtil.mine() // test
      } yield {

        // verify preparation
        mostRecent shouldBe None

        // verify
        minedBlock shouldBe None

      }
        , timeout)

    }

    scenario("most recent block exists but unmined hashes is empty") {

      Await.result(
      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock()
        mostRecent <- miningUtil.mostRecentBlock()

        // test
        minedBlock <- miningUtil.mine()

      } yield {

        // verify preparation
        mostRecent shouldBe 'isDefined

        // verify
        minedBlock shouldBe None
      }
        , timeout)

    }

    scenario("most recent block exists and there's unmined hashes") {

      logger.info("begin test")
      Await.result(
        for {

        // prepare
          genesis <- BlockGenerator.createGenesisBlock()
          mostRecent <- miningUtil.mostRecentBlock()
          unminedHashesSize <- HashGenerator.createXManyUnminedHashesFuture(100)

          // test
          minedBlock <- miningUtil.mine()

        } yield {

          // verify preparation
          mostRecent shouldBe 'isDefined
          logger.info("verify preparation")

          // verify
          minedBlock shouldBe None
          logger.info("finished test")

        }
        , timeout)

    }

    scenario("mine 10 blocks in a row to make sure their numbers are correctly incremented") {

      Await.result(
      BlockGenerator.createGenesisBlock() map { genesis =>

        // TODO FIXME: loop is not executed
        for (i <- 1 to 10) {
          logger.info(s"mine block: number $i")
          HashGenerator.createXManyUnminedHashesFuture(5000) map { sizeUnminedHashes =>

            // test
            miningUtil.mine() map {

              // verify
              case None => fail("mining did not result in a new block")
              case Some(block) => block.number should be(i)

            }

          }
        }

      }
        , timeout)

    }

  }

  feature("MiningUtil.checkMiningTriggers") {

    scenario("sizeCheck: false; ageCheck: false") {

      Await.result(
      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock()
        sizeCheck <- miningUtil.sizeCheck()
        ageCheck <- miningUtil.ageCheck()

        // test
        checkTriggers <- miningUtil.checkMiningTriggers()

      } yield {

        // verify preparation
        sizeCheck shouldBe false
        ageCheck shouldBe false

        // verify
        checkTriggers shouldBe false

      }
        , timeout)

    }

    scenario("sizeCheck: true; ageCheck: false") {

      Await.result(
      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock()
        unminedHashesSize <- HashGenerator.createUnminedHashesFuture(sizeCheckResultsInTrue = true)
        sizeCheck <- miningUtil.sizeCheck()
        ageCheck <- miningUtil.ageCheck()

        // test
        checkTriggers <- miningUtil.checkMiningTriggers()

      } yield {

        // verify preparation
        sizeCheck shouldBe true
        ageCheck shouldBe false

        // verify
        checkTriggers shouldBe true

      }
        , timeout)


    }

    scenario("sizeCheck: false; ageCheck: true") {

      Await.result(
      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true)
        unminedHashesSize <- HashGenerator.createUnminedHashesFuture(sizeCheckResultsInTrue = false)
        sizeCheck <- miningUtil.sizeCheck()
        ageCheck <- miningUtil.ageCheck()

        // test
        checkTriggers <- miningUtil.checkMiningTriggers()

      } yield {

        // verify preparation
        sizeCheck shouldBe false
        ageCheck shouldBe true

        // verify
        checkTriggers shouldBe true

      }
        , timeout)

    }

    scenario("sizeCheck: true; ageCheck: true") {

      Await.result(
      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true)
        unminedHashesSize <- HashGenerator.createUnminedHashesFuture(sizeCheckResultsInTrue = true)
        sizeCheck <- miningUtil.sizeCheck()
        ageCheck <- miningUtil.ageCheck()

        // test
        checkTriggers <- miningUtil.checkMiningTriggers()

      } yield {

        // verify preparation
        sizeCheck shouldBe true
        ageCheck shouldBe true

        // verify
        checkTriggers shouldBe true

      }
        , timeout)

    }

  }

  feature("MiningUtil.sizeCheck") {

    scenario("no unmined hashes") {

      Await.result(
      for {
        currentUnmined <- ChainStorageServiceClient.unminedHashes() // prepare
        sizeCheck <- miningUtil.sizeCheck() // test
      } yield {

        // verify prepare
        currentUnmined.hashes shouldBe 'isEmpty

        // verify
        sizeCheck shouldBe false

      }
        , timeout)

    }

    scenario("size of unmined hashes below threshold") {

      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = false)

      Await.result(
      for {
        sizeCheck <- miningUtil.sizeCheck()
      } yield {
        sizeCheck shouldBe false
      }
      , timeout)

    }

    scenario("size of unmined hashes above threshold") {

      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = true)

      Await.result(
      for {
        sizeCheck <- miningUtil.sizeCheck()
      } yield {
        sizeCheck shouldBe true
      }
      , timeout)

    }

  }

  feature("MiningUtil.ageCheck") {

    scenario("no blocks, not even the genesis block") {

      Await.result(
      for {
        genesis <- ChainStorageServiceClient.getGenesisBlock // prepare
        ageCheck <- miningUtil.ageCheck() // test
      } yield {

        genesis shouldBe None // verify prepare
        ageCheck shouldBe false // verify

      }
        , timeout)

    }

    scenario("has only a genesis block (too new)") {

      Await.result(
      for {
        genesis <- BlockGenerator.createGenesisBlock() // prepare
        ageCheck <- miningUtil.ageCheck() // test
      } yield {
        ageCheck shouldBe false
      }
        , timeout)

    }

    scenario("has only a genesis block (old enough)") {

      Await.result(
      for {
        genesis <- BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true) // prepare
        ageCheck <- miningUtil.ageCheck() // test
      } yield {
        ageCheck shouldBe true
      }
        , timeout)

    }

    scenario("has genesis block and one regular block (too new)") {

      Await.result(
      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock()
        fullBlock <- BlockGenerator.generateMinedBlock()

        // test
        ageCheck <- miningUtil.ageCheck()

      } yield {
        ageCheck shouldBe false
      }
        , timeout)

    }

    scenario("has genesis block and one regular block (old enough)") {

      val mineEveryXSeconds = Config.mineEveryXSeconds
      val createdBlock = DateUtil.nowUTC.minusSeconds(mineEveryXSeconds)

      Await.result(
      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true)
        fullBlock <- BlockGenerator.generateFullBlock(genesis.hash, genesis.number, created = createdBlock)

        // test
        ageCheck <- miningUtil.ageCheck()

      } yield {
        // verify
        ageCheck shouldBe true
      }
        , timeout)

    }

  }

  feature("MiningUtil.mostRecentBlock") {

    scenario("no blocks, not even the genesis block") {

      Await.result(
      for {
        genesis <- ChainStorageServiceClient.getGenesisBlock // prepare
        mostRecent <- miningUtil.mostRecentBlock() // test
      } yield {

        genesis shouldBe None // verify preparation
        mostRecent shouldBe None // verify

      }
        , timeout)

    }

    scenario("has only a genesis block") {

      Await.result(
      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock()

        // test
        mostRecentOpt <- miningUtil.mostRecentBlock()

      } yield {

        // verify
        mostRecentOpt.isDefined shouldBe true

        val mostRecent = mostRecentOpt.get
        val expected = BaseBlockInfo(genesis.hash, genesis.number, genesis.created, genesis.version)
        mostRecent shouldBe expected

      }
        , timeout)

    }

    scenario("has genesis block and one regular block") {

      Await.result(
      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock()
        block <- BlockGenerator.generateMinedBlock()

        // test
        mostRecentOpt <- miningUtil.mostRecentBlock()

      } yield {

        // verify
        mostRecentOpt.isDefined shouldBe true

        val mostRecent = mostRecentOpt.get
        val expected = BaseBlockInfo(block.hash, block.number, block.created, block.version)
        mostRecent shouldBe expected

      }
        , timeout)

    }

  }

}
