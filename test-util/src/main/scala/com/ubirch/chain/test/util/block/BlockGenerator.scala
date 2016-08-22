package com.ubirch.chain.test.util.block

import com.ubirch.backend.chain.model.{Data, FullBlock}
import com.ubirch.chain.share.util.{HashRouteUtil, MiningUtil}
import com.ubirch.util.crypto.hash.HashUtil
import org.scalatest.FeatureSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-08-22
  */
object BlockGenerator extends FeatureSpec {

  def generateMinedBlock(maxElementCount: Int): Future[FullBlock] = {

    val hashes = HashUtil.randomSha256Hashes(maxElementCount) map(Data(_))
    hashes foreach (new HashRouteUtil).hash

    (new MiningUtil).mine() map {
      case None => fail("failed to generate a mined block")
      case Some(fullBlock) => fullBlock
    }

  }

}
