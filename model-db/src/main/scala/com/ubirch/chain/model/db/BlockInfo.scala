package com.ubirch.chain.model.db

import com.ubirch.chain.model.db.util.ChainType

import org.joda.time.DateTime

/**
  * author: cvandrei
  * since: 2017-06-29
  */
case class BlockInfo(blockHash: String,
                     chainType: String = ChainType.BIGCHAIN,
                     anchors: Set[Anchor] = Set.empty,
                     created: DateTime = DateTime.now
                    )
