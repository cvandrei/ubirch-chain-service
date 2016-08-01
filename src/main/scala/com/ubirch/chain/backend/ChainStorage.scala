package com.ubirch.chain.backend

import com.ubirch.chain.backend.util.HashUtil
import com.ubirch.chain.json.AnchorType._
import com.ubirch.chain.json.{Anchor, BlockInfo, HashInfo, HashResponse}
import org.joda.time.DateTime

/**
  * author: cvandrei
  * since: 2016-08-01
  */
trait ChainStorage {

  def storeHash(hash: String): HashResponse = {
    // TODO implementation instead of the current dummy
    HashResponse(hash, DateTime.now)
  }

  def getHash(hash: String): HashInfo = {
    // TODO implementation instead of the current dummy
    HashInfo(hash = hash)
  }

  def getBlock(hash: String): BlockInfo = {

    // TODO implementation instead of the current dummy
    val previousHash = HashUtil.hexString(s"previous block hash - $hash")
    val bitcoinAnchorHash = HashUtil.hexString(s"bitcoin anchor - $hash")
    val ubirchAnchorHash = HashUtil.hexString(s"ubirch anchor - $hash")

    BlockInfo(
      hash,
      previousBlockHash = previousHash,
      anchors = Seq(
        Anchor(bitcoin, bitcoinAnchorHash),
        Anchor(ubirch, ubirchAnchorHash)
      )
    )

  }

  def getFullBlock(hash: String): BlockInfo = {

    // TODO implementation instead of the current dummy
    val previousHash = HashUtil.hexString(s"previous block hash - $hash")
    val bitcoinAnchorHash = HashUtil.hexString(s"bitcoin anchor - $hash")
    val ubirchAnchorHash = HashUtil.hexString(s"ubirch anchor - $hash")

    BlockInfo(
      hash,
      previousBlockHash = previousHash,
      anchors = Seq(
        Anchor(bitcoin, bitcoinAnchorHash),
        Anchor(ubirch, ubirchAnchorHash)
      ),
      hashes = Some(Seq("123", "456", "789"))
    )

  }

}
