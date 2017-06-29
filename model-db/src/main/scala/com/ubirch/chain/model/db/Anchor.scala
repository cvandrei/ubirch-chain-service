package com.ubirch.chain.model.db

import org.joda.time.{DateTime, DateTimeZone}

/**
  * author: cvandrei
  * since: 2017-06-29
  */
case class Anchor(hash: String,
                  chainType: String = "bitcoin",
                  created: DateTime = DateTime.now(DateTimeZone.UTC)
                 )
