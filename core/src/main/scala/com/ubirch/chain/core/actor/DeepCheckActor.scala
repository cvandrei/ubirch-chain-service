package com.ubirch.chain.core.actor

import com.ubirch.chain.core.actor.util.ActorTools
import com.ubirch.chain.core.manager.DeepCheckManager
import com.ubirch.util.deepCheck.model.{DeepCheckRequest, DeepCheckResponse}

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-06-07
  */
class DeepCheckActor extends Actor
  with ActorLogging {

  override def receive: Receive = {

    case _: DeepCheckRequest =>
      val sender = context.sender()
      deepCheck() map (sender ! _)

    case _ => log.error("unknown message")

  }

  private def deepCheck(): Future[DeepCheckResponse] = DeepCheckManager.connectivityCheck()

}

object DeepCheckActor extends ActorTools {

  def props(): Props = roundRobin().props(Props(new DeepCheckActor()))

  def actor()(implicit _system: ActorSystem): ActorRef = _system.actorOf(props(), ActorNames.DEEP_CHECK)

}
