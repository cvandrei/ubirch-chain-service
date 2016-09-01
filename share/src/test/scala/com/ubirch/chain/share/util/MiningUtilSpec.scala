package com.ubirch.chain.share.util

import com.ubirch.chain.config.Config
import com.ubirch.chain.share.testutil.{BlockGenerator, HashGenerator}
import com.ubirch.chain.test.base.ElasticSearchSpec
import com.ubirch.client.storage.ChainStorageServiceClient
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * author: cvandrei
  * since: 2016-08-30
  */
class MiningUtilSpec extends ElasticSearchSpec {

  private val miningUtil = new MiningUtil

  feature("MiningUtil.blockCheck") {

    scenario("trigger = false") {

      for {
        mostRecent <- miningUtil.mostRecentBlock()
        blockCheck <- miningUtil.blockCheck() // test
      } yield {

        mostRecent shouldBe None // verify preparation
        blockCheck shouldBe None // verify

      }

    }

    scenario("trigger = true") {

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

    }

    scenario("most recent block exists and there's unmined hashes") {

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

        // verify
        minedBlock shouldBe None

      }

    }

  }

  feature("MiningUtil.checkMiningTriggers") {

    scenario("sizeCheck: false; ageCheck: false") {

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

    }

    scenario("sizeCheck: true; ageCheck: false") {

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


    }

    scenario("sizeCheck: false; ageCheck: true") {

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

    }

    scenario("sizeCheck: true; ageCheck: true") {

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

      for {
        genesis <- BlockGenerator.createGenesisBlock() // prepare
        ageCheck <- miningUtil.ageCheck() // test
      } yield {
        ageCheck shouldBe false
      }

    }

    scenario("has only a genesis block (old enough)") {

      for {
        genesis <- BlockGenerator.createGenesisBlock(ageCheckResultsInTrue = true) // prepare
        ageCheck <- miningUtil.ageCheck() // test
      } yield {
        ageCheck shouldBe true
      }

    }

    scenario("has genesis block and one regular block (too new)") {

      for {

      // prepare
        genesis <- BlockGenerator.createGenesisBlock()
        fullBlock <- BlockGenerator.generateMinedBlock()

        // test
        ageCheck <- miningUtil.ageCheck()

      } yield {
        ageCheck shouldBe false
      }

    }

    scenario("has genesis block and one regular block (old enough)") {

      val mineEveryXSeconds = Config.mineEveryXSeconds
      val createdBlock = DateTime.now.minusSeconds(mineEveryXSeconds)

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

    }

    scenario("has genesis block and one regular block") {

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

    }

  }

}
