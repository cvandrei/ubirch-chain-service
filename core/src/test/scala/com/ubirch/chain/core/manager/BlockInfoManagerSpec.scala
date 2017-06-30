package com.ubirch.chain.core.manager

import com.ubirch.chain.config.ChainConfig
import com.ubirch.chain.model.db.{Anchor, BlockInfo}
import com.ubirch.chain.testTools.db.mongo.MongoSpec

/**
  * author: cvandrei
  * since: 2017-06-30
  */
class BlockInfoManagerSpec extends MongoSpec {

  private val collection = ChainConfig.mongoChainServiceCollectionAnchors

  feature("findByBlockHash()") {

    scenario("empty database") {

      // test
      BlockInfoManager.findByBlockHash("6670b0e12d04f9464c1a1467220bed6f734a8fd5f7b5cbc570ea5097973b693f") flatMap { result =>

        // verify
        result should be('isEmpty)
        mongoTestUtils.countAll(collection) map (_ shouldBe 0)

      }

    }

    scenario("some records exist but not the one we're looking for") {

      // prepare
      val blockInfo1 = BlockInfo(
        blockHash = "6670b0e12d04f9464c1a1467220bed6f734a8fd5f7b5cbc570ea5097973b693f",
        anchors = Set(Anchor(hash = "a5e17b775376cb6e8ceae3b40ab09d0c982568bbf67265425d228a30ad6f063f"))
      )
      val blockInfo2 = BlockInfo(
        blockHash = "6670b0e12d04f9464c1a1467220bed6f734a8fd5f7b5cbc570ea5097973b693g",
        anchors = Set(Anchor(hash = "a5e17b775376cb6e8ceae3b40ab09d0c982568bbf67265425d228a30ad6f063g"))
      )

      BlockInfoManager.create(blockInfo1) flatMap { prep1 =>

        prep1 should be(Some(blockInfo1))

        BlockInfoManager.create(blockInfo2) flatMap { prep2 =>

          prep2 should be(Some(blockInfo2))
          mongoTestUtils.countAll(collection) flatMap { count =>

            count shouldBe 2

            // test && verify
            BlockInfoManager.findByBlockHash("6670b0e12d04f9464c1a1467220bed6f734a8fd5f7b5cbc570ea5097973b693a") map { result =>
              result should be('isEmpty)
            }

          }

        }

      }

    }

    scenario("blockHash exists") {

      // prepare
      val blockInfo1 = BlockInfo(
        blockHash = "6670b0e12d04f9464c1a1467220bed6f734a8fd5f7b5cbc570ea5097973b693f",
        anchors = Set(Anchor(hash = "a5e17b775376cb6e8ceae3b40ab09d0c982568bbf67265425d228a30ad6f063f"))
      )
      BlockInfoManager.create(blockInfo1) flatMap { prep1 =>

        prep1 should be(Some(blockInfo1))

        mongoTestUtils.countAll(collection) flatMap { count =>

          count shouldBe 1

          // test && verify
          BlockInfoManager.findByBlockHash(blockInfo1.blockHash) map { result =>
            result should be(Some(blockInfo1))
          }

        }

      }

    }

  }

  feature("create()") {

    scenario("record with blockHash does not exist") {

      // prepare
      val anchor1 = Anchor(hash = "a5e17b775376cb6e8ceae3b40ab09d0c982568bbf67265425d228a30ad6f063f")
      val blockInfo = BlockInfo(
        blockHash = "6670b0e12d04f9464c1a1467220bed6f734a8fd5f7b5cbc570ea5097973b693f",
        anchors = Set(anchor1)
      )

      // test
      BlockInfoManager.create(blockInfo) flatMap {

        // verify
        case None => fail("failed to create block")

        case Some(created) =>
          created should be(blockInfo)
          mongoTestUtils.countAll(collection) map (_ shouldBe 1)
          BlockInfoManager.findByBlockHash(blockInfo.blockHash) map (_ should be(Some(blockInfo)))

      }

    }

    scenario("record with blockHash already exists") {

      // prepare
      val anchor1 = Anchor(hash = "a5e17b775376cb6e8ceae3b40ab09d0c982568bbf67265425d228a30ad6f063f")
      val blockInfo = BlockInfo(
        blockHash = "6670b0e12d04f9464c1a1467220bed6f734a8fd5f7b5cbc570ea5097973b693f",
        anchors = Set(anchor1)
      )

      BlockInfoManager.create(blockInfo) flatMap {

        case None => fail("failed to prepare existing BlockInfo")

        case Some(_) =>

          // test
          BlockInfoManager.create(blockInfo) flatMap {

            // verify
            case None => mongoTestUtils.countAll(collection) map (_ shouldBe 1)
            case Some(created) => fail(s"should not have created a block: created=$created")

          }

      }

    }

  }

}
