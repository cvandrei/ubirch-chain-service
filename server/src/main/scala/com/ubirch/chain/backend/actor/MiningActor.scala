package com.ubirch.chain.backend.actor

import akka.actor.Actor
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.ubirch.chain.share.util.MiningUtil

/**
  * author: cvandrei
  * since: 2016-08-03
  */
class MiningActor extends Actor {

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val miningUtil = new MiningUtil

  override def receive: Receive = {

    case bc: BlockCheck => miningUtil.blockCheck()

  }

}

case class BlockCheck()
