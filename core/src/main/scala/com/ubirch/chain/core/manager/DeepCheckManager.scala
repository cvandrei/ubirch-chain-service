package com.ubirch.chain.core.manager

import com.ubirch.util.deepCheck.model.DeepCheckResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-06-09
  */
object DeepCheckManager {

  def connectivityCheck(): Future[DeepCheckResponse] = {
    // TODO check server health
    Future(DeepCheckResponse())
  }

}
