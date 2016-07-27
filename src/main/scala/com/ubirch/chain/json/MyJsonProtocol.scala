package com.ubirch.chain.json

import org.json4s._
import org.json4s.ext.JavaTypesSerializers
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._

/**
  * author: cvandrei
  * since: 2016-07-27
  */
object MyJsonProtocol {

  implicit def json4sJacksonFormats: Formats = DefaultFormats ++ JavaTypesSerializers.all

  def toJsonFromObject(anyRef: AnyRef): JValue = Extraction.decompose(anyRef)

  def toJsonFromString(json: String): JValue = parse(json)

  def toStringJson(anyRef: AnyRef): String = write(anyRef)

}
