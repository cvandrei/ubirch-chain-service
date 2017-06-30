package com.ubirch.chain.model.db

import com.ubirch.chain.model.db.util.ChainType

import org.joda.time.DateTime

/**
  * author: cvandrei
  * since: 2017-06-29
  */
case class Anchor(hash: String,
                  chainType: String = ChainType.BITCOIN,
                  created: DateTime = DateTime.now
                 )
