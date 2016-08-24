package com.ubirch.chain.share.util

import com.ubirch.backend.chain.model.{HashRequest, HashedData}
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

  /**
    * Hashes the input and stores it for future mining.
    *
    * @param input the data to hash
    * @return None if input data is invalid; a hash otherwise
    */
  def hash(input: HashRequest): Future[Option[HashedData]] = {

    invalidData.contains(input.data) match {

      case true => Future(None)

      case false =>
        val hash = HashedData(HashUtil.sha256HexString(input.data))
        ChainStorageServiceClient.storeHash(hash)

    }

  }

}
