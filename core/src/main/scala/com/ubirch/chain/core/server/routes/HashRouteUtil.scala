package com.ubirch.chain.core.server.routes

import com.ubirch.chain.core.storage.ChainStorage
import com.ubirch.chain.json.{Data, Hash}
import com.ubirch.util.crypto.hash.HashUtil

/**
  * author: cvandrei
  * since: 2016-08-17
  */
class HashRouteUtil extends ChainStorage {

  private val invalidData: Set[String] = Set("")

  def hash(input: Data): Option[Hash] = {

    invalidData.contains(input.data) match {

      case true => None

      case false =>
        val hash = HashUtil.sha256HexString(input.data)
        val hashObject = Hash(hash)
        storeHash(hashObject)
        Some(hashObject)

    }

  }

}
