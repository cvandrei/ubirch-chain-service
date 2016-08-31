package com.ubirch.chain.share.util

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.backend.chain.model.FullBlock
import com.ubirch.chain.config.{Config, ConfigKeys}
import com.ubirch.chain.share.merkle.BlockUtil
import com.ubirch.client.storage.ChainStorageServiceClient
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-08-16
  */
class MiningUtil extends LazyLogging {

  /**
    * Check if the next block needs to be mined and mine it if the answer is yes.
    *
    * @return the newly mined block; None if mining did not trigger
    */
  def blockCheck(): Future[Option[FullBlock]] = {

    checkMiningTriggers() flatMap {
      case true => mine()
      case false => Future(None)
    }

  }

  /**
    * Mine the next block.
    *
    * @return the newly block; None if an error occurred (see logfile for details)
    */
  def mine(): Future[Option[FullBlock]] = {

    mostRecentBlock() flatMap {

      case None =>
        logger.error("found no most recent block")
        Future(None)

      case Some(mostRecentBlock) =>

        ChainStorageServiceClient.unminedHashes() flatMap { unmined =>

          val previousBlockHash = mostRecentBlock.hash
          val hashes = unmined.hashes
          val newNumber = mostRecentBlock.number + 1

          val newBlock = BlockUtil.newBlock(previousBlockHash, newNumber, hashes)
          val blockHash = newBlock.hash
          logger.info(s"new block: hash=$blockHash, number=$newNumber (${hashes.size} hashes; ${BlockUtil.size(hashes) / 1000} kb)")

          ChainStorageServiceClient.upsertFullBlock(newBlock) flatMap {

            case None =>
              logger.error("failed to insert new block")
              Future(None)

            case Some(upsertedBlock) =>
              // TODO FIXME switch to Seq instead of Set !!!
              ChainStorageServiceClient.deleteHashes(hashes.toSet) map {

                case true => Some(upsertedBlock)

                case false =>
                  logger.error(s"failed to delete newly mined hashes from unmined list: newBlock.hash=${upsertedBlock.hash}")
                  Some(upsertedBlock)

              }

          }

        }

    }

  }

  /**
    * Checks if mining may be triggered.
    *
    * @return true if mining may be triggered; false otherwise
    */
  def checkMiningTriggers(): Future[Boolean] = {

    for {
      sizeTrigger <- sizeCheck()
      ageTrigger <- ageCheck()
    } yield {
      sizeTrigger || ageTrigger
    }

  }

  /**
    * Mining can be triggered by the size of unmined hashes and this method checks if the maximum size for a block has
    * been reached.
    *
    * @return true if size based trigger fires; false otherwise
    */
  def sizeCheck(): Future[Boolean] = {

    val maxBlockSizeBytes = Config.blockMaxSizeByte
    val maxBlockSizeKB = Config.blockMaxSizeKB
    logger.debug(s"checking size of unmined hashes: ${ConfigKeys.BLOCK_MAX_SIZE} = $maxBlockSizeKB kb")

    ChainStorageServiceClient.unminedHashes() map { hashes =>

      val size = BlockUtil.size(hashes.hashes)
      size >= maxBlockSizeBytes match {

        case true =>
          val sizeKb = size / 1000
          logger.info(s"trigger mining of new block (size) -- ${hashes.hashes.length} hashes ($sizeKb kb)")
          true

        case false => false

      }

    }

  }

  /**
    * Mining can be triggered by the age of the most recent block and this method checks if the most recent block has
    * reached it's maximum age.
    *
    * @return true if time based trigger fires; false otherwise
    */
  def ageCheck(): Future[Boolean] = {

    mostRecentBlock() map {

      case None =>

        logger.error("found no most recent block")
        false

      case Some(block) =>

        val nextCreationDate = block.created.plusSeconds(Config.mineEveryXSeconds)
        nextCreationDate.isBeforeNow match {

          case true =>
            logger.info(s"trigger mining of new block (time) -- mostRecentBlock.created=${block.created}, nextCreationDate=$nextCreationDate")
            true

          case false =>
            logger.debug(s"do not trigger mining of new block (time) -- mostRecentBlock.created=${block.created}, nextCreationDate=$nextCreationDate")
            false

        }

    }

  }

  /**
    * Gives us basic information about the most recent block.
    *
    * @return most recent block infos if one exists; None otherwise
    */
  def mostRecentBlock(): Future[Option[BaseBlockInfo]] = {

    ChainStorageServiceClient.mostRecentBlock() flatMap {

      case None =>

        ChainStorageServiceClient.getGenesisBlock map {
          case None => None
          case Some(genesis) => Some(BaseBlockInfo(genesis.hash, genesis.number, genesis.created, genesis.version))
        }

      case Some(block) => Future(Some(BaseBlockInfo(block.hash, block.number, block.created, block.version)))

    }

  }

}

case class BaseBlockInfo(hash: String,
                         number: Long,
                         created: DateTime,
                         version: String
                        )
