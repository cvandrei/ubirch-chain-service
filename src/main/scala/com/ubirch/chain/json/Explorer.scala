package com.ubirch.chain.json

import com.ubirch.chain.backend.util.ChainConstants
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

/**
  * @param hash              hash of the block
  * @param created           when the block was created
  * @param version           in which version of ubirchChainService was this block created
  * @param previousBlockHash hash of the previous block
  * @param anchors           optional list of anchors to other chains
  * @param hashes            list of hashes included in the block (only set if you requested the full block)
  */
case class BlockInfo(hash: String,
                     created: DateTime = DateTime.now,
                     version: String = ChainConstants.v1,
                     previousBlockHash: String,
                     anchors: Seq[Anchor] = Seq.empty,
                     hashes: Option[Seq[String]] = None // only set if you requested a full block
                    )

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
                  version: String = ChainConstants.v1
                 )
