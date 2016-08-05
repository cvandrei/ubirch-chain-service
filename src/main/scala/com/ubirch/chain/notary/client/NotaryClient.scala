package com.ubirch.chain.notary.client

import java.net.URL

import com.ubirch.chain.backend.config.Config
import uk.co.bigbeeconsultants.http.HttpClient
import uk.co.bigbeeconsultants.http.header.HeaderName._
import uk.co.bigbeeconsultants.http.header.Headers
import uk.co.bigbeeconsultants.http.header.MediaType._
import uk.co.bigbeeconsultants.http.request.RequestBody

/**
  * author: cvandrei
  * since: 2016-08-05
  */
object NotaryClient {

  def notarize(blockHash: String): NotarizeResponse = {

    val notarizeUrl = new URL(Config.anchorUrl)
    val json = s"""{"data": "$blockHash"}"""
    val body = RequestBody(json, APPLICATION_JSON)
    val headers = Headers(CONTENT_TYPE -> APPLICATION_JSON)

    val httpClient = new HttpClient
    val res = httpClient.post(notarizeUrl, Some(body), headers)
    // TODO remaining code

    NotarizeResponse(blockHash)

  }

}

case class NotarizeResponse(hash: String)
