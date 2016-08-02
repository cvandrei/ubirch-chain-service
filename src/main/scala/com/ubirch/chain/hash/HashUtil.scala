package com.ubirch.chain.hash

import com.roundeights.hasher.Implicits._
import com.roundeights.hasher.{Digest, Hash}
import com.ubirch.chain.backend.config.Config

/**
  * author: cvandrei
  * since: 2016-07-28
  */
object HashUtil {

  def digest(data: String): Digest = {

    Config.hashAlgorithm match {

      case "md5" => data.md5
      case "sha1" => data.sha1
      case "sha256" => data.sha256
      case "sha384" => data.sha384
      case "sha512" => data.sha512
      case "bcrypt" => data.bcrypt
      case "crc32" => data.crc32

    }

  }

  def hexString(data: String): String = digest(data).hex

  def byteArray(data: String): Array[Byte] = digest(data).bytes

  def hashAsBytes(buf: String): Array[Byte] = Hash(buf).bytes

  def hashAsHex(buf: Array[Byte]): String = Hash(buf).hex

}
