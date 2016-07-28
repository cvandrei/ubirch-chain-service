package com.ubirch.chain.json.hash

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
                     previousBlockHash: String,
                     created: DateTime = DateTime.now,
                     version: String = ChainConstants.v1,
                     anchors: Seq[Anchor] = Seq.empty
                    )

case class Anchor(anchorTo: String,
                  hash: String,
                  created: DateTime = DateTime.now,
                  version: String = ChainConstants.v1
                 )

object AnchorType {

  val ubirch = "ubirchBlockChain"

  val bitcoin = "Bitcoin"

}