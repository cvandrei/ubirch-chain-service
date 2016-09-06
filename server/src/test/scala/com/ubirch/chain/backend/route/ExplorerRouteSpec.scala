package com.ubirch.chain.backend.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import com.ubirch.backend.chain.model.{BlockInfo, FullBlock, HashInfo, HashRequest}
import com.ubirch.chain.core.server.util.RouteConstants
import com.ubirch.chain.share.util.HashRouteUtil
import com.ubirch.chain.test.base.RouteSpec
import com.ubirch.chain.test.util.BlockGenerator
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-08-22
  */
class ExplorerRouteSpec extends RouteSpec {

  private val routes = (new MainRoute).myRoute
  private val hashRouteUtil = new HashRouteUtil

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
      for {
        hashRes <- hashRouteUtil.hash(data)
      } yield {

        hashRes shouldNot be(None)
        val hash = hashRes.get.hash
        Thread.sleep(500)

        // test
        Get(RouteConstants.urlExplorerEventHash(hash)) ~> routes ~> check {
          status shouldEqual OK
          responseAs[HashInfo].hash shouldEqual hash
        }
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

      for {
        genesis <- BlockGenerator.createGenesisBlock()
        block <- BlockGenerator.generateMinedBlock()
      } yield {

        val hash = block.hash

        Get(RouteConstants.urlExplorerBlockInfo(hash)) ~> routes ~> check {
          status shouldEqual OK
          responseAs[BlockInfo].hash shouldEqual hash
        }

      }

    }

  }

  feature(s"call byPreviousBlockHash explorer route: ${RouteConstants.urlExplorerBlockInfoByPreviousPrefix}/:hash") {

    scenario("GET without hash in address") {
      Get(RouteConstants.urlExplorerBlockInfoByPrevious("")) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET unknown hash") {
      val hash = "1111222233334444555566667777888899990000aaaabbbbccccddddeeeeffff"
      Get(RouteConstants.urlExplorerBlockInfoByPrevious(hash)) ~> Route.seal(routes) ~> check {
        status shouldEqual NotFound
      }
    }

    scenario("GET known hash") {

      for {
        genesis <- BlockGenerator.createGenesisBlock()
        fullBlock <- BlockGenerator.generateMinedBlock()
      } yield {

        Get(RouteConstants.urlExplorerBlockInfoByPrevious(fullBlock.previousBlockHash)) ~> routes ~> check {
          status shouldEqual OK
          val resBlock = responseAs[BlockInfo]
          resBlock.hash shouldEqual fullBlock.hash
          resBlock.previousBlockHash shouldEqual fullBlock.previousBlockHash
        }

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

      for {

        genesis <- BlockGenerator.createGenesisBlock()
        block <- BlockGenerator.generateMinedBlock()

      } yield {

        val hash = block.hash

        Get(RouteConstants.urlExplorerFullBlock(hash)) ~> routes ~> check {
          status shouldEqual OK
          val fullBlock = responseAs[FullBlock]
          fullBlock.hash shouldEqual hash
          fullBlock.hashes.get contains block.hashes.get.head
        }

      }

    }

  }

}
