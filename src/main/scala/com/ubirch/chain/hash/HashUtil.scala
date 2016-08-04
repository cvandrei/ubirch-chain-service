package com.ubirch.chain.hash

import com.roundeights.hasher.Implicits._
import com.roundeights.hasher.{Digest, Hash}

/**
  * author: cvandrei
  * since: 2016-07-28
  */
object HashUtil {

  def digest(data: String): Digest = data.sha256

  def hexString(data: String): String = digest(data).hex

  def byteArray(data: String): Array[Byte] = digest(data).bytes

  def hashAsBytes(buf: String): Array[Byte] = Hash(buf).bytes

  def hashAsHex(buf: Array[Byte]): String = Hash(buf).hex

}
