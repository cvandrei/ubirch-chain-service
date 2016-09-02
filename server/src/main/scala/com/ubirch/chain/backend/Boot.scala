package com.ubirch.chain.backend

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.backend.actor.{AnchorActor, AnchorNow, BlockCheck, GenesisActor, GenesisCheck, MiningActor}
import com.ubirch.chain.backend.route.MainRoute
import com.ubirch.chain.config.{Config, ConfigKeys}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * author: cvandrei
  * since: 2016-07-26
  */
object Boot extends App with LazyLogging {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  private val miningActor = system.actorOf(Props[MiningActor])
  private val anchorActor = system.actorOf(Props[AnchorActor])
  private val genesisActor = system.actorOf(Props[GenesisActor])

  logger.info("ubirchChainService started")

  implicit val timeout = Timeout(15 seconds)
  (genesisActor ? GenesisCheck) map {

    case Success(result) => logger.info(s"genesis block check successful")

    case Failure(failure) =>
      logger.error("failed to check if genesis block exists or create one if not")
      throw failure

  }

  val bindingFuture = start()
  logGeneralMiningInfo()
  scheduleMiningRelatedJobs()

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run() = {
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }
  })

  def start(): Future[ServerBinding] = {

    val interface = Config.interface
    val port = Config.port
    implicit val timeout = Timeout(5, TimeUnit.SECONDS)

    Http().bindAndHandle((new MainRoute).myRoute, interface, port)

  }

  private def logGeneralMiningInfo(): Unit = {

    val mineEveryXSeconds = Config.mineEveryXSeconds
    val maxBlockSize = Config.blockMaxSizeKB
    logger.info(s"block mining params: $mineEveryXSeconds s; $maxBlockSize kb")

  }

  private def scheduleMiningRelatedJobs(): Unit = {
    scheduleMining()
    scheduleAnchoring()
  }

  private def scheduleMining(): Unit = {

    val miningInitialDelay = 2
    logger.info(s"schedule next block check (${ConfigKeys.BLOCK_CHECK_INTERVAL}) to run in $miningInitialDelay seconds")
    system.scheduler.scheduleOnce(miningInitialDelay seconds, miningActor, new BlockCheck())

  }

  private def scheduleAnchoring(): Unit = {

    Config.anchorEnabled match {

      case true =>
        val anchorInterval = Config.anchorInterval
        logger.info(s"schedule anchoring (${ConfigKeys.ANCHOR_INTERVAL}) to run every $anchorInterval seconds")
        system.scheduler.schedule(10 seconds, anchorInterval seconds, anchorActor, new AnchorNow())

      case false =>
        logger.info(s"anchoring is disabled in configuration")

    }

  }

}
