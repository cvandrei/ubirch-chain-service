package com.ubirch.chain.model.db

import org.joda.time.{DateTime, DateTimeZone}

/**
  * author: cvandrei
  * since: 2017-06-29
  */
case class BlockAnchor(blockId: String,
                       chainType: String = "bigchain",
                       anchors: Set[Anchor] = Set.empty,
                       created: DateTime = DateTime.now(DateTimeZone.UTC)
                      )
