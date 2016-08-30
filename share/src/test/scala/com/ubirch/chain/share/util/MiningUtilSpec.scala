package com.ubirch.chain.share.util

import com.ubirch.chain.config.Config
import com.ubirch.chain.share.testutil.{BlockGenerator, HashGenerator}
import com.ubirch.chain.test.base.ElasticSearchSpec
import com.ubirch.client.storage.ChainStorageServiceClient
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2016-08-30
  */
class MiningUtilSpec extends ElasticSearchSpec {

  val miningUtil = new MiningUtil
  val awaitTimeout: FiniteDuration = 10 seconds

  ignore("MiningUtil.blockCheck") {
    // TODO write tests
  }

  feature("MiningUtil.mine") {

    scenario("most recent block does not exist") {

      // prepare
      Await.result(miningUtil.mostRecentBlock(), awaitTimeout) shouldBe None

      // test
      for {
        minedBlock <- miningUtil.mine()
      } yield {
        // verify
        minedBlock shouldBe None
      }

    }

    scenario("most recent block exists but unmined hashes is empty") {

      // prepare
      BlockGenerator.createGenesisBlock()
      Await.result(miningUtil.mostRecentBlock(), awaitTimeout).isDefined shouldBe true

      // test
      for {
        minedBlock <- miningUtil.mine()
      } yield {
        // verify
        minedBlock shouldBe None
      }

    }

    scenario("most recent block exists and there's unmined hashes") {

      // prepare
      BlockGenerator.createGenesisBlock()
      Await.result(miningUtil.mostRecentBlock(), awaitTimeout).isDefined shouldBe true
      HashGenerator.createXManyUnminedHashes(500)

      // test
      for {
        minedBlock <- miningUtil.mine()
      } yield {
        // verify
        minedBlock shouldBe None
      }

    }

  }

  feature("MiningUtil.checkMiningTriggers") {

    scenario("sizeCheck: false; ageCheck: false") {

      // prepare
      BlockGenerator.createGenesisBlock()

      Await.result(miningUtil.sizeCheck(), awaitTimeout) shouldBe false
      Await.result(miningUtil.ageCheck(), awaitTimeout) shouldBe false

      // test & verify
      for {
        checkTriggers <- miningUtil.checkMiningTriggers()
      } yield {
        checkTriggers shouldBe false
      }

    }

    scenario("sizeCheck: true; ageCheck: false") {

      // prepare
      BlockGenerator.createGenesisBlock()
      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = true)

      Await.result(miningUtil.sizeCheck(), awaitTimeout) shouldBe true
      Await.result(miningUtil.ageCheck(), awaitTimeout) shouldBe false

      // test & verify
      for {
        checkTriggers <- miningUtil.checkMiningTriggers()
      } yield {
        checkTriggers shouldBe true
      }
    }

    scenario("sizeCheck: false; ageCheck: true") {

      // prepare
      BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true)
      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = false)

      Await.result(miningUtil.sizeCheck(), awaitTimeout) shouldBe false
      Await.result(miningUtil.ageCheck(), awaitTimeout) shouldBe true

      // test & verify
      for {
        checkTriggers <- miningUtil.checkMiningTriggers()
      } yield {
        checkTriggers shouldBe true
      }

    }

    scenario("sizeCheck: true; ageCheck: true") {

      // prepare
      BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true)
      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = true)

      Await.result(miningUtil.sizeCheck(), awaitTimeout) shouldBe true
      Await.result(miningUtil.ageCheck(), awaitTimeout) shouldBe true

      // test & verify
      for {
        checkTriggers <- miningUtil.checkMiningTriggers()
      } yield {
        checkTriggers shouldBe true
      }

    }

  }

  feature("MiningUtil.sizeCheck") {

    scenario("no unmined hashes") {

      // prepare
      val currentUnmined = Await.result(ChainStorageServiceClient.unminedHashes(), 3 seconds)
      currentUnmined.hashes shouldBe 'isEmpty

      // test
      for {
        sizeCheck <- miningUtil.sizeCheck()
      } yield {
        // verify
        sizeCheck shouldBe false
      }

    }

    scenario("size of unmined hashes below threshold") {

      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = false)

      for {
        sizeCheck <- miningUtil.sizeCheck()
      } yield {
        sizeCheck shouldBe true
      }

    }

    scenario("size of unmined hashes above threshold") {

      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = true)

      for {
        sizeCheck <- miningUtil.sizeCheck()
      } yield {
        sizeCheck shouldBe false
      }

    }

  }

  feature("MiningUtil.ageCheck") {

    scenario("no blocks, not even the genesis block") {

      Await.result(ChainStorageServiceClient.getGenesisBlock, awaitTimeout) shouldBe None

      for {
        ageCheck <- miningUtil.ageCheck()
      } yield {
        ageCheck shouldBe false
      }

    }

    scenario("has only a genesis block (too new)") {

      // prepare
      BlockGenerator.createGenesisBlock()

      for {
        ageCheck <- miningUtil.ageCheck()
      } yield {
        ageCheck shouldBe false
      }

    }

    scenario("has only a genesis block (old enough)") {

      // prepare
      BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true)

      for {
        ageCheck <- miningUtil.ageCheck()
      } yield {
        ageCheck shouldBe true
      }

    }

    scenario("has genesis block and one regular block (too new)") {

      // prepare
      BlockGenerator.createGenesisBlock()
      BlockGenerator.generateMinedBlock()

      for {
        ageCheck <- miningUtil.ageCheck()
      } yield {
        ageCheck shouldBe false
      }

    }

    scenario("has genesis block and one regular block (old enough)") {

      // prepare
      val mineEveryXSeconds = Config.mineEveryXSeconds

      val genesis = BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true)

      val createdBlock = DateTime.now.minusSeconds(mineEveryXSeconds)
      BlockGenerator.generateFullBlock(genesis.hash, genesis.number, 1000, createdBlock)

      // test
      for {
        ageCheck <- miningUtil.ageCheck()
      } yield {
        // verify
        ageCheck shouldBe true
      }

    }

  }

  feature("MiningUtil.mostRecentBlock") {

    scenario("no blocks, not even the genesis block") {

      Await.result(ChainStorageServiceClient.getGenesisBlock, awaitTimeout) shouldBe None

      for {
        mostRecent <- miningUtil.mostRecentBlock()
      } yield {
        mostRecent shouldBe None
      }

    }

    scenario("has only a genesis block") {

      // prepare
      val genesis = BlockGenerator.createGenesisBlock()

      // test
      for {
        mostRecentOpt <- miningUtil.mostRecentBlock()
      } yield {

        // verify
        mostRecentOpt.isDefined shouldBe true

        val mostRecent = mostRecentOpt.get
        val expected = BaseBlockInfo(genesis.hash, genesis.number, genesis.created, genesis.version)
        mostRecent shouldBe expected

      }

    }

    scenario("has genesis block and one regular block") {

      // prepare
      BlockGenerator.createGenesisBlock()
      val block = BlockGenerator.generateMinedBlock()

      // test
      for {
        mostRecentOpt <- miningUtil.mostRecentBlock()
      } yield {

        // verify
        mostRecentOpt.isDefined shouldBe true

        val mostRecent = mostRecentOpt.get
        val expected = BaseBlockInfo(block.hash, block.number, block.created, block.version)
        mostRecent shouldBe expected

      }

    }

  }

}
