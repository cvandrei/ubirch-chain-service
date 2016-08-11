package com.ubirch.chain.json

import com.ubirch.chain.share.RouteConstants
import org.joda.time.DateTime

/**
  * author: cvandrei
  * since: 2016-07-28
  */

/**
  * @param hash      hash value
  * @param blockHash hash of the block it is part of (None if not mined yet)
  */
case class HashInfo(hash: String,
                    blockHash: Option[String] = None
                   )

trait BaseBlock {
  /** hash of the block **/
  val hash: String
  /** when the block was created **/
  val created: DateTime = DateTime.now
  /** in which version of ubirchChainService was this block created **/
  val version: String = RouteConstants.v1
  /** list of hashes included in the block (only set if you requested the full block) **/
  val hashes: Option[Seq[String]] = None // only set if you requested a full block
}

case class GenesisBlock(hash: String,
                        override val hashes: Option[Seq[String]] = None,
                        override val created: DateTime
                       ) extends BaseBlock

trait PreviousBlockReference {
  /** hash of the previous block **/
  val previousBlockHash: String
}

/**
  * @param hash              hash of the block
  * @param previousBlockHash hash of the previous block
  * @param anchors           optional list of anchors to other chains
  * @param hashes            list of hashes included in the block (only set if you requested the full block)
  */
case class BlockInfo(hash: String,
                     previousBlockHash: String,
                     anchors: Seq[Anchor] = Seq.empty,
                     override val hashes: Option[Seq[String]] = None
                    ) extends BaseBlock with PreviousBlockReference

/**
  * @param hashes list of unmined hashes
  */
case class UnminedHashes(hashes: Seq[String] = Seq.empty)

/**
  * @param anchorTo which blockchain we anchor into
  * @param hash     hash of the anchor transaction
  * @param created  creation time
  * @param version  version of ubirchChainService that created the anchor
  */
case class Anchor(anchorTo: String,
                  hash: String,
                  created: DateTime = DateTime.now,
                  version: String = RouteConstants.v1
                 )
