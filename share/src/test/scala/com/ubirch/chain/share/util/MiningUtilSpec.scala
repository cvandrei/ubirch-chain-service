package com.ubirch.chain.share.util

import com.ubirch.chain.share.testutil.{BlockGenerator, HashGenerator}
import com.ubirch.chain.test.base.ElasticSearchSpec
import com.ubirch.client.storage.ChainStorageServiceClient

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

  ignore("MiningUtil.blockCheck") {
    // TODO write tests
  }

  ignore("MiningUtil.mine") {
    // TODO write tests
  }

  ignore("MiningUtil.checkMiningTriggers") {
    // TODO write tests
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

      HashGenerator.createUnminedHashes(belowSizeThreshold = true)

      for {
        sizeCheck <- miningUtil.sizeCheck()
      } yield {
        sizeCheck shouldBe true
      }

    }

    scenario("size of unmined hashes above threshold") {

      HashGenerator.createUnminedHashes(belowSizeThreshold = false)

      for {
        sizeCheck <- miningUtil.sizeCheck()
      } yield {
        sizeCheck shouldBe false
      }

    }

  }

  ignore("MiningUtil.ageCheck") {
    // TODO write tests
  }

  feature("MiningUtil.mostRecentBlock") {

    scenario("no blocks, not even the genesis block") {

      Await.result(ChainStorageServiceClient.getGenesisBlock, 2 seconds) shouldBe None

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

    scenario("has genesis block and one regular block afterwards") {

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
