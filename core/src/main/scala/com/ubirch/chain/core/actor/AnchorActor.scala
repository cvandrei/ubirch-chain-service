package com.ubirch.chain.core.actor

import com.ubirch.chain.config.ChainConfig
import com.ubirch.chain.core.manager.BlockManager
import com.ubirch.chain.model.db.bigchain.BigchainBlockInfo
import com.ubirch.util.mongo.connection.MongoUtil

import akka.actor.{Actor, ActorLogging, Props}

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-06-29
  */
class AnchorActor(implicit mongo: MongoUtil) extends Actor with ActorLogging {

  implicit private val ec = context.dispatcher

  override def receive: Receive = {

    case _: BlockCheck =>

      log.debug("checking if we have a block that needs to be anchored")
      BlockManager.findLatestBlockInfo() map {

        case None => log.info("found no latest BigchainDb block")

        case Some(blockInfo: BigchainBlockInfo) =>

          // TODO check if this block has been anchored yet
          // TODO if not: anchor with notary-service (blockInfo.id)
          // TODO use _BlockAnchor_ and _Anchor_ models
          log.debug(s"found a latest BigchainDb block: $blockInfo")

      }

      val anchorInterval = ChainConfig.anchorInterval
      log.debug(s"rescheduling AnchorActor to run in $anchorInterval seconds")
      context.system.scheduler.scheduleOnce(anchorInterval seconds, self, BlockCheck())

    case _ => log.error("unknown message")

  }

}

object AnchorActor extends {
  def props()(implicit mongo: MongoUtil): Props = Props(new AnchorActor())
}

case class BlockCheck()
