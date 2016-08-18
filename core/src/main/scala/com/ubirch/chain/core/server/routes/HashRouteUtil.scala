package com.ubirch.chain.core.server.routes

import com.ubirch.chain.json.{Data, Hash}
import com.ubirch.client.storage.ChainStorageServiceClient._
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
        storeHash(hash) map {
          case None => None
          case Some(storedHash) => Some(Hash(hash))
        }

    }

  }

}
