package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import com.ubirch.backend.chain.model.{BlockInfo, FullBlock, HashRequest}
import com.ubirch.chain.core.server.util.RouteConstants
import com.ubirch.chain.share.util.{HashRouteUtil, MiningUtil}
import com.ubirch.chain.test.base.RouteSpec
import com.ubirch.chain.test.util.{BlockGenerator, HashGenerator}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2016-08-22
  */
class ExplorerRouteSpec extends RouteSpec {

  private val routes = (new MainRoute).myRoute
  private val hashRouteUtil = new HashRouteUtil
  private val miningUtil = new MiningUtil

  private val timeoutShort = 10 seconds

  feature(s"call hash explorer route: ${RouteConstants.urlExplorerEventHashPrefix}/:hash") {

    scenario("GET without hash in address") {
      Get(RouteConstants.urlExplorerEventHash("")) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET unknown hash") {
      val hash = "1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff"
      Get(RouteConstants.urlExplorerEventHash(hash)) ~> routes ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET known hash") {

      // prepare
      val data = HashRequest("""{"foo": {"bar": 42}}""")
      val hashRes = Await.result(hashRouteUtil.hash(data), timeoutShort)
      hashRes shouldNot be(None)
      val hash = hashRes.get.hash

      HashGenerator.createXManyUnminedHashes(200)
      Await.result(BlockGenerator.createGenesisBlock(), timeoutShort)

      val blockOpt = Await.result(miningUtil.mine(), timeoutShort)
      Await.result(miningUtil.waitUntilBlockIndexed(blockOpt.get.hash), timeoutShort)

      // test
      Get(RouteConstants.urlExplorerEventHash(hash)) ~> routes ~> check {
        // verify
        status shouldEqual OK
        responseAs[BlockInfo].hash shouldEqual blockOpt.get.hash
      }

    }

  }

  feature(s"call blockInfo explorer route: ${RouteConstants.urlExplorerBlockInfoPrefix}/:hash") {

    scenario("GET without hash in address") {
      Get(RouteConstants.urlExplorerBlockInfo("")) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET unknown hash") {
      val hash = "1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff"
      Get(RouteConstants.urlExplorerBlockInfo(hash)) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET known hash") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeoutShort)
      val minedBlock = Await.result(BlockGenerator.generateMinedBlock(), timeoutShort)

      val hash = minedBlock.hash

      // test
      Get(RouteConstants.urlExplorerBlockInfo(hash)) ~> routes ~> check {
        // verify
        status shouldEqual OK
        responseAs[BlockInfo].hash shouldEqual hash
      }

    }

  }

  feature(s"call byPreviousBlockHash explorer route: ${RouteConstants.urlExplorerNextBlockInfoPrefix}/:hash") {

    scenario("GET without hash in address") {
      Get(RouteConstants.urlExplorerNextBlockInfo("")) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET unknown hash") {
      val hash = "1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff"
      Get(RouteConstants.urlExplorerNextBlockInfo(hash)) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET known hash") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeoutShort)
      val minedBlock = Await.result(BlockGenerator.generateMinedBlock(), timeoutShort)

      // test
      Get(RouteConstants.urlExplorerNextBlockInfo(minedBlock.previousBlockHash)) ~> routes ~> check {

        // verify
        status shouldEqual OK
        val resBlock = responseAs[BlockInfo]
        resBlock.hash shouldEqual minedBlock.hash
        resBlock.previousBlockHash shouldEqual minedBlock.previousBlockHash

      }

    }

  }

  feature(s"call fullBlock explorer route: ${RouteConstants.urlExplorerFullBlockPrefix}/:hash") {

    scenario("GET without hash in address") {
      Get(RouteConstants.urlExplorerFullBlock("")) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET unknown hash") {
      val hash = "1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff"
      Get(RouteConstants.urlExplorerFullBlock(hash)) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET known hash") {

      // prepare
      Await.result(BlockGenerator.createGenesisBlock(), timeoutShort)
      val minedBlock = Await.result(BlockGenerator.generateMinedBlock(), timeoutShort)

      val hash = minedBlock.hash

      // test
      Get(RouteConstants.urlExplorerFullBlock(hash)) ~> routes ~> check {

        // verify
        status shouldEqual OK
        val fullBlock = responseAs[FullBlock]
        fullBlock.hash shouldEqual hash
        fullBlock.hashes.get shouldEqual minedBlock.hashes.get

      }

    }

  }

}
