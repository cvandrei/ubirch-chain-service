package com.ubirch.chain.share.util

import com.ubirch.backend.chain.model.{Data, Hash}
import com.ubirch.client.storage.ChainStorageServiceClient
import com.ubirch.util.crypto.hash.HashUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-08-17
  */
class HashRouteUtil {

  private val invalidData: Set[String] = Set("")

  def hash(input: Data): Future[Option[Hash]] = {

    invalidData.contains(input.data) match {

      case true => Future(None)

      case false =>
        val hash = HashUtil.sha256HexString(input.data)
        ChainStorageServiceClient.storeHash(hash) map {
          case None => None
          case Some(storedHash) => Some(Hash(hash))
        }

    }

  }

}
