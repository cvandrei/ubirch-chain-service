package com.ubirch.chain.model.rest

import org.json4s.JValue

/**
  * author: cvandrei
  * since: 2017-06-23
  */
case class Transaction(msg: Option[JValue],
                       hash: Option[String]
                      )
