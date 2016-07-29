package com.ubirch.chain.json

import com.ubirch.chain.backend.util.ChainConstants
import org.joda.time.DateTime

/**
  * author: cvandrei
  * since: 2016-07-28
  */
case class HashInfo(hash: String,
                    blockHash: Option[String] = None,
                    created: DateTime = DateTime.now,
                    version: String = ChainConstants.v1
                   )

case class BlockInfo(hash: String,
                     created: DateTime = DateTime.now,
                     version: String = ChainConstants.v1,
                     previousBlockHash: String,
                     anchors: Seq[Anchor] = Seq.empty,
                     hashes: Option[Seq[String]] = None // only set if you requested a full block
                    )

case class Anchor(anchorTo: String,
                  hash: String,
                  created: DateTime = DateTime.now,
                  version: String = ChainConstants.v1
                 )
