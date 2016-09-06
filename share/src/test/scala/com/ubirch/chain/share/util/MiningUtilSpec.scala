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

        }, timeout)

    }

    scenario("trigger = true") {

      // prepare
      val genesis = Await.result(
        for {
          genesis <- BlockGenerator.createGenesisBlock()
          unminedHashesSize <- HashGenerator.createUnminedHashesFuture(sizeCheckResultsInTrue = true)
        } yield {

          val mostRecent = Await.result(miningUtil.mostRecentBlock(), timeout)
          mostRecent.get.hash shouldBe genesis.hash
          genesis

        }, timeout)

      Await.result(
        for {
          blockCheck <- miningUtil.blockCheck() // test
        } yield {

          // verify
          blockCheck shouldBe 'isDefined
          blockCheck.get.previousBlockHash shouldBe genesis.hash

        }, timeout)

    }

  }

  feature("MiningUtil.mine") {

    scenario("most recent block does not exist") {

      // prepare
      val mostRecent = Await.result(miningUtil.mostRecentBlock(), timeout)
      mostRecent shouldBe None

      Await.result(
        for {
          minedBlock <- miningUtil.mine() // test
        } yield {

          minedBlock shouldBe None // verify

        }
        , timeout)

    }

    scenario("most recent block exists but unmined hashes is empty") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeout)
      val mostRecent = Await.result(miningUtil.mostRecentBlock(), timeout)
      mostRecent shouldBe 'isDefined
      val unmined = Await.result(ChainStorageServiceClient.unminedHashes(), timeout)
      unmined.hashes shouldBe 'isEmpty

      Await.result(
        for {
          minedBlock <- miningUtil.mine() // test
        } yield {

          minedBlock shouldBe None // verify

        }, timeout)

    }

    scenario("most recent block exists and there's unmined hashes") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeout)
      val mostRecent = Await.result(miningUtil.mostRecentBlock(), timeout)
      mostRecent shouldBe 'isDefined
      Await.result(HashGenerator.createXManyUnminedHashesFuture(100), timeout)

      Await.result(
        for {
          minedBlock <- miningUtil.mine() // test
        } yield {

          minedBlock shouldNot be(None) // verify

        }, timeout)

    }

    scenario("mine 10 blocks in a row to make sure their numbers are correctly incremented") {

      Await.result(
        BlockGenerator.createGenesisBlock() map { genesis =>

          for (i <- 1 to 10) {
            logger.info(s"mine block: number $i")
            HashGenerator.createXManyUnminedHashesFuture(1000) map { sizeUnminedHashes =>

              // test
              miningUtil.mine() map {

                // verify
                case None => fail("mining did not result in a new block")
                case Some(block) => block.number should be(i)

              }

            }
          }

        }, timeout)

    }

  }

  feature("MiningUtil.checkMiningTriggers") {

    scenario("sizeCheck: false; ageCheck: false") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeout)
      val sizeCheck = Await.result(miningUtil.sizeCheck(), timeout)
      sizeCheck shouldBe false
      val ageCheck = Await.result(miningUtil.ageCheck(), timeout)
      ageCheck shouldBe false

      Await.result(
        for {
          checkTriggers <- miningUtil.checkMiningTriggers() // test
        } yield {

          checkTriggers shouldBe false // verify

        }, timeout)

    }

    scenario("sizeCheck: true; ageCheck: false") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeout)
      Await.result(HashGenerator.createUnminedHashesFuture(sizeCheckResultsInTrue = true), timeout)
      val sizeCheck = Await.result(miningUtil.sizeCheck(), timeout)
      sizeCheck shouldBe true
      val ageCheck = Await.result(miningUtil.ageCheck(), timeout)
      ageCheck shouldBe false

      Await.result(
        for {
          checkTriggers <- miningUtil.checkMiningTriggers() // test
        } yield {

          checkTriggers shouldBe true // verify

        }, timeout)


    }

    scenario("sizeCheck: false; ageCheck: true") {

      // prepare
      Await.result(
        for {
          genesis <- BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true)
          unminedHashesSize <- HashGenerator.createUnminedHashesFuture(sizeCheckResultsInTrue = false)
        } yield {

          val sizeCheck = Await.result(miningUtil.sizeCheck(), timeout)
          sizeCheck shouldBe false
          val ageCheck = Await.result(miningUtil.ageCheck(), timeout)
          ageCheck shouldBe true

        }, timeout)

      Await.result(
        for {
          checkTriggers <- miningUtil.checkMiningTriggers() // test
        } yield {

          checkTriggers shouldBe true // verify

        }, timeout)

    }

    scenario("sizeCheck: true; ageCheck: true") {

      // prepare
      Await.result(
        for {
          genesis <- BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true)
          unminedHashesSize <- HashGenerator.createUnminedHashesFuture(sizeCheckResultsInTrue = true)
        } yield {

          val sizeCheck = Await.result(miningUtil.sizeCheck(), timeout)
          sizeCheck shouldBe true
          val ageCheck = Await.result(miningUtil.ageCheck(), timeout)
          ageCheck shouldBe true

        }, timeout)

      Await.result(
        for {
          checkTriggers <- miningUtil.checkMiningTriggers() // test
        } yield {

          checkTriggers shouldBe true // verify

        }, timeout)

    }

  }

  feature("MiningUtil.sizeCheck") {

    scenario("no unmined hashes") {

      // prepare
      val currentUnmined = Await.result(ChainStorageServiceClient.unminedHashes(), timeout)
      currentUnmined.hashes shouldBe 'isEmpty

      Await.result(
        for {
          sizeCheck <- miningUtil.sizeCheck() // test
        } yield {

          sizeCheck shouldBe false // verify

        }, timeout)

    }

    scenario("size of unmined hashes below threshold") {

      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = false)

      Await.result(
        for {
          sizeCheck <- miningUtil.sizeCheck()
        } yield {

          sizeCheck shouldBe false

        }, timeout)

    }

    scenario("size of unmined hashes above threshold") {

      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = true)

      Await.result(
        for {
          sizeCheck <- miningUtil.sizeCheck()
        } yield {

          sizeCheck shouldBe true

        }, timeout)

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

        }, timeout)

    }

    scenario("has only a genesis block (too new)") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeout)

      Await.result(
        for {
          ageCheck <- miningUtil.ageCheck() // test
        } yield {

          ageCheck shouldBe false // verify

        }, timeout)

    }

    scenario("has only a genesis block (old enough)") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true), timeout)

      Await.result(
        for {
          ageCheck <- miningUtil.ageCheck() // test
        } yield {

          ageCheck shouldBe true // verify

        }, timeout)

    }

    scenario("has genesis block and one regular block (too new)") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeout)
      Await.result(BlockGenerator.generateMinedBlock(), timeout)

      Await.result(
        for {
          ageCheck <- miningUtil.ageCheck() // test
        } yield {

          ageCheck shouldBe false // verify

        }, timeout)

    }

    scenario("has genesis block and one regular block (old enough)") {

      // prepare
      val mineEveryXSeconds = Config.mineEveryXSeconds
      val createdBlock = DateUtil.nowUTC.minusSeconds(mineEveryXSeconds)

      val genesis = Await.result(BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true), timeout)
      Await.result(BlockGenerator.generateFullBlock(genesis.hash, genesis.number, created = createdBlock), timeout)

      Await.result(
        for {
          ageCheck <- miningUtil.ageCheck() // test
        } yield {

          ageCheck shouldBe true // verify

        }, timeout)

    }

  }

  feature("MiningUtil.mostRecentBlock") {

    scenario("no blocks, not even the genesis block") {

      val genesis = Await.result(ChainStorageServiceClient.getGenesisBlock, timeout)
      genesis shouldBe None

      Await.result(
        for {
          mostRecent <- miningUtil.mostRecentBlock() // test
        } yield {

          mostRecent shouldBe None // verify

        }, timeout)

    }

    scenario("has only a genesis block") {

      // prepare
      val genesis = Await.result(BlockGenerator.createGenesisBlock(), timeout)

      Await.result(
        for {
          mostRecentOpt <- miningUtil.mostRecentBlock() // test
        } yield {

          // verify
          mostRecentOpt.isDefined shouldBe true

          val mostRecent = mostRecentOpt.get
          val expected = BaseBlockInfo(genesis.hash, genesis.number, genesis.created, genesis.version)
          mostRecent shouldBe expected

        }, timeout)

    }

    scenario("has genesis block and one regular block") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeout)
      val block = Await.result(BlockGenerator.generateMinedBlock(), timeout)

      Await.result(
        for {
          mostRecentOpt <- miningUtil.mostRecentBlock() // test
        } yield {

          // verify
          mostRecentOpt.isDefined shouldBe true

          val mostRecent = mostRecentOpt.get
          val expected = BaseBlockInfo(block.hash, block.number, block.created, block.version)
          mostRecent shouldBe expected

        }, timeout)

    }

  }

}
