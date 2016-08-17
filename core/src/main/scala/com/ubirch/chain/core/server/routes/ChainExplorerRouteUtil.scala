package com.ubirch.chain.core.server.routes

import com.ubirch.chain.core.storage.ChainStorage
import com.ubirch.chain.json.{BlockInfo, Hash, HashInfo}

/**
  * author: cvandrei
  * since: 2016-08-17
  */
class ChainExplorerRouteUtil extends ChainStorage {

  def hash(hash: String): Option[HashInfo] = {

    val hashObject = Hash(hash)
    getHashInfo(hashObject) match {
      case None => None
      case Some(hashInfo) => Some(hashInfo)
    }

  }

  def block(hash: String): Option[BlockInfo] = {

    val hashObject = Hash(hash)
    getBlock(hashObject) match {
      case None => None
      case Some(blockInfo) => Some(blockInfo)
    }

  }

  def fullBlock(hash: String): Option[BlockInfo] = {

    val hashObject = Hash(hash)
    getBlockWithHashes(hashObject) match {
      case None => None
      case Some(blockInfo) => Some(blockInfo)
    }

  }

}
