package com.ubirch.chain.server

import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.chain.config.{ChainConfig, ChainConfigKeys}
import com.ubirch.chain.core.actor.{ActorNames, AnchorActor, BlockCheck}
import com.ubirch.chain.core.manager.server.QueueManager
import com.ubirch.chain.server.route.MainRoute
import com.ubirch.util.mongo.connection.MongoUtil

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-03-22
  */
object Boot extends App with StrictLogging {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  implicit val timeout = Timeout(ChainConfig.actorTimeout seconds)

  implicit val mongoBigchain: MongoUtil = new MongoUtil(ChainConfigKeys.MONGO_BIGCHAIN_PREFIX)
  implicit val mongoChain: MongoUtil = new MongoUtil(ChainConfigKeys.MONGO_CHAIN_SERVICE_PREFIX)

  private val anchorActor = system.actorOf(
    props = AnchorActor.props()(mongoBigchain = mongoBigchain, mongoChain = mongoChain),
    name = ActorNames.ANCHOR
  )

  val bindingFuture = start()
  registerShutdownHooks()

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = {
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }
  })

  def start(): Future[ServerBinding] = {

    val interface = ChainConfig.interface
    val port = ChainConfig.port
    implicit val timeout = Timeout(5, TimeUnit.SECONDS)

    QueueManager.initConsumers()
    scheduleAnchoring()

    logger.info(s"start http server on $interface:$port")
    Http().bindAndHandle(
      new MainRoute()(_system = system, mongoBigchain = mongoBigchain, mongoChain = mongoChain).myRoute,
      interface,
      port
    )

  }

  private def registerShutdownHooks() = {

    Runtime.getRuntime.addShutdownHook(new Thread() {

      override def run(): Unit = {

        bindingFuture
          .flatMap(_.unbind())
          .onComplete(_ => system.terminate())

        mongoBigchain.close()
        mongoChain.close()

      }

    })

  }

  private def scheduleAnchoring(): Unit = {

    if (ChainConfig.anchorEnabled) {

      val anchorInterval = ChainConfig.anchorInterval
      val schedulerOffset = ChainConfig.anchorSchedulerOffset
      logger.info(s"schedule anchoring (${ChainConfigKeys.ANCHOR_INTERVAL}) to run every $anchorInterval seconds (schedulerOffset is $schedulerOffset seconds")
      system.scheduler.schedule(schedulerOffset seconds, anchorInterval seconds, anchorActor, BlockCheck())

    } else {
      logger.info(s"anchoring is disabled in configuration")
    }

  }

}
