package com.ubirch.chain.core.actor

import com.ubirch.chain.core.actor.util.ActorTools
import com.ubirch.chain.core.manager.server.DeepCheckManager
import com.ubirch.util.deepCheck.model.{DeepCheckRequest, DeepCheckResponse}
import com.ubirch.util.mongo.connection.MongoUtil

import akka.actor.{Actor, ActorLogging, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-06-07
  */
class DeepCheckActor(implicit mongoBigchain: MongoUtil, mongoChain: MongoUtil) extends Actor
  with ActorLogging {

  override def receive: Receive = {

    case _: DeepCheckRequest =>
      val sender = context.sender()
      deepCheck() map (sender ! _)

    case _ => log.error("unknown message")

  }

  private def deepCheck(): Future[DeepCheckResponse] = DeepCheckManager.connectivityCheck()(mongoBigchain = mongoBigchain, mongoChain = mongoChain)

}

object DeepCheckActor extends ActorTools {

  def props()(implicit mongoBigchain: MongoUtil, mongoChain: MongoUtil): Props = {
    roundRobin().props(
      Props(
        new DeepCheckActor()(mongoBigchain = mongoBigchain, mongoChain = mongoChain)
      )
    )
  }

}
