package com.ubirch.chain.notary.client

import java.net.URL

import com.ubirch.chain.backend.config.Config
import com.ubirch.chain.json.Hash
import com.ubirch.chain.json.util.MyJsonProtocol
import org.json4s.native.JsonMethods._
import uk.co.bigbeeconsultants.http.HttpClient
import uk.co.bigbeeconsultants.http.header.MediaType._
import uk.co.bigbeeconsultants.http.request.RequestBody

/**
  * author: cvandrei
  * since: 2016-08-05
  */
object NotaryClient extends MyJsonProtocol {

  def notarize(blockHash: String): NotarizeResponse = {

    val notarizeUrl = new URL(Config.anchorUrl)
    val json = s"""{"data": "$blockHash"}"""
    val body = RequestBody(json, APPLICATION_JSON)

    val httpClient = new HttpClient
    val res = httpClient.post(notarizeUrl, Some(body))
    val hash = parse(res.body.asString).extract[Hash]

    NotarizeResponse(hash.hash)

  }

}

case class NotarizeResponse(hash: String)
