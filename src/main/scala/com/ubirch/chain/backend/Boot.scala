package com.ubirch.chain.backend

import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.backend.config.AppConst
import com.ubirch.chain.backend.service.ChainServiceActor
import spray.can.Http

/**
  * author: cvandrei
  * since: 2016-07-26
  */
object Boot extends App with LazyLogging {

  logger.info("cdbeidService started")
  val system = start()

  def start(): ActorSystem = {

    val config = ConfigFactory.load
    val interface = config.getString(AppConst.INTERFACE)
    val port = config.getInt(AppConst.PORT)

    implicit val system = ActorSystem("on-spray-can")
    val service = system.actorOf(Props[ChainServiceActor], "ubirchChainService")

    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    IO(Http) ! Http.Bind(service, interface = interface, port = port)

    system

  }

}
