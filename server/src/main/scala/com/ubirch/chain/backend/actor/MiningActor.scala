package com.ubirch.chain.backend.actor

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.backend.Boot._
import com.ubirch.chain.config.{ConfigKeys, Config}
import com.ubirch.chain.share.util.MiningUtil

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2016-08-03
  */
class MiningActor extends Actor
  with LazyLogging {

  private val miningUtil = new MiningUtil

  override def receive: Receive = {

    case bc: BlockCheck =>
      miningUtil.blockCheck().onComplete {
        case _ =>
          val blockCheckInterval = Config.blockCheckInterval
          logger.info(s"schedule next block check (${ConfigKeys.BLOCK_CHECK_INTERVAL}) to run in $blockCheckInterval seconds")
          context.system.scheduler.scheduleOnce(blockCheckInterval seconds, self, new BlockCheck())
      }

  }

}

case class BlockCheck()
