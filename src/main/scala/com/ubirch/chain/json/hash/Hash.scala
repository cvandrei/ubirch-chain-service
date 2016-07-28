package com.ubirch.chain.json.hash

import org.joda.time.DateTime

/**
  * author: cvandrei
  * since: 2016-07-28
  */
case class Envelope(data: String)

case class HashResponse(hash: String,
                        timestamp: DateTime = DateTime.now
                       )
