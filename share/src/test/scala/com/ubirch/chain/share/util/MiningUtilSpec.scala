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

  feature("MiningUtil.blockCheck") {

    scenario("trigger = false") {

      // prepare
      Await.result(miningUtil.mostRecentBlock(), awaitTimeout) shouldBe None

      // test
      for {
        blockCheck <- miningUtil.blockCheck()
      } yield {
        // verify
        blockCheck shouldBe None
      }

    }

    scenario("trigger = true") {

      // prepare
      val genesisHash = BlockGenerator.createGenesisBlock().hash
      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = true)

      Await.result(miningUtil.mostRecentBlock(), awaitTimeout).get.hash shouldBe genesisHash

      // test
      for {
        blockCheck <- miningUtil.blockCheck()
      } yield {
        // verify
        blockCheck shouldBe Some
        blockCheck.get.previousBlockHash shouldBe genesisHash
      }

    }

  }

  feature("MiningUtil.mine") {

    scenario("most recent block does not exist") {

      for {
        mostRecent <- miningUtil.mostRecentBlock() // prepare
        minedBlock <- miningUtil.mine() // test
      } yield {

        // verify preparation
        mostRecent shouldBe None

        // verify
        minedBlock shouldBe None

      }

    }

    scenario("most recent block exists but unmined hashes is empty") {

      // prepare
      BlockGenerator.createGenesisBlock()

      for {
        mostRecent <- miningUtil.mostRecentBlock() // prepare
        minedBlock <- miningUtil.mine() // test
      } yield {

        // verify preparation
        mostRecent shouldBe 'isDefined

        // verify
        minedBlock shouldBe None
      }

    }

    scenario("most recent block exists and there's unmined hashes") {

      // prepare
      BlockGenerator.createGenesisBlock()
      HashGenerator.createXManyUnminedHashes(500)

      for {
        mostRecent <- miningUtil.mostRecentBlock() // prepare
        minedBlock <- miningUtil.mine() // test
      } yield {

        // verify preparation
        mostRecent shouldBe 'isDefined

        // verify
        minedBlock shouldBe None

      }

    }

  }

  feature("MiningUtil.checkMiningTriggers") {

    scenario("sizeCheck: false; ageCheck: false") {

      // prepare
      BlockGenerator.createGenesisBlock()

      for {
        sizeCheck <- miningUtil.sizeCheck()
        ageCheck <- miningUtil.ageCheck()
        checkTriggers <- miningUtil.checkMiningTriggers() // test
      } yield {

        // verify preparation
        sizeCheck shouldBe false
        ageCheck shouldBe false

        // verify
        checkTriggers shouldBe false

      }

    }

    scenario("sizeCheck: true; ageCheck: false") {

      // prepare
      BlockGenerator.createGenesisBlock()
      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = true)

      for {
        sizeCheck <- miningUtil.sizeCheck()
        ageCheck <- miningUtil.ageCheck()
        checkTriggers <- miningUtil.checkMiningTriggers() // test
      } yield {

        // verify preparation
        sizeCheck shouldBe true
        ageCheck shouldBe false

        // verify
        checkTriggers shouldBe true

      }


    }

    scenario("sizeCheck: false; ageCheck: true") {

      // prepare
      BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true)
      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = false)

      for {
        sizeCheck <- miningUtil.sizeCheck()
        ageCheck <- miningUtil.ageCheck()
        checkTriggers <- miningUtil.checkMiningTriggers() // test
      } yield {

        // verify preparation
        sizeCheck shouldBe false
        ageCheck shouldBe true

        // verify
        checkTriggers shouldBe true

      }

    }

    scenario("sizeCheck: true; ageCheck: true") {

      // prepare
      BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true)
      HashGenerator.createUnminedHashes(sizeCheckResultsInTrue = true)

      for {
        sizeCheck <- miningUtil.sizeCheck()
        ageCheck <- miningUtil.ageCheck()
        checkTriggers <- miningUtil.checkMiningTriggers() // test
      } yield {

        // verify preparation
        sizeCheck shouldBe true
        ageCheck shouldBe true

        // verify
        checkTriggers shouldBe true

      }

    }

  }

  feature("MiningUtil.sizeCheck") {

    scenario("no unmined hashes") {

      for {
        currentUnmined <- ChainStorageServiceClient.unminedHashes() // prepare
        sizeCheck <- miningUtil.sizeCheck() // test
      } yield {

        // verify prepare
        currentUnmined.hashes shouldBe 'isEmpty

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

      for {
        genesis <- ChainStorageServiceClient.getGenesisBlock // prepare
        ageCheck <- miningUtil.ageCheck() // test
      } yield {

        genesis shouldBe None // verify prepare
        ageCheck shouldBe false // verify

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

      for {
        genesis <- ChainStorageServiceClient.getGenesisBlock // prepare
        mostRecent <- miningUtil.mostRecentBlock() // test
      } yield {

        genesis shouldBe None // verify preparation
        mostRecent shouldBe None // verify

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
