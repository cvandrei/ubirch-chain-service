package com.ubirch.chain.notary.client

import java.net.URL

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.chain.backend.config.Config
import com.ubirch.chain.json.util.MyJsonProtocol
import org.json4s.native.JsonMethods._
import uk.co.bigbeeconsultants.http.HttpClient
import uk.co.bigbeeconsultants.http.header.MediaType._
import uk.co.bigbeeconsultants.http.request.RequestBody
import uk.co.bigbeeconsultants.http.response.Status._

/**
  * author: cvandrei
  * since: 2016-08-05
  */
object NotaryClient extends MyJsonProtocol
with LazyLogging {

  def notarize(blockHash: String): Option[NotarizeResponse] = {

    val notarizeUrl = new URL(Config.anchorUrl)
    val json = s"""{"data": "$blockHash"}"""
    val body = RequestBody(json, APPLICATION_JSON)

    val httpClient = new HttpClient
    val res = httpClient.post(notarizeUrl, Some(body))

    res.status match {

      case S200_OK => Some(parse(res.body.asString).extract[NotarizeResponse])

      case _ =>
        logger.error(s"failed to notarize: blockHash=$blockHash, response=$res")
        None

    }


  }

}

case class NotarizeResponse(hash: String)
