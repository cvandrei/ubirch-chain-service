package com.ubirch.chain.backend.util

import com.roundeights.hasher.{Digest, Hasher}
import com.ubirch.chain.backend.config.Config

/**
  * author: cvandrei
  * since: 2016-07-28
  */
object HashUtil {

  def digest(data: String): Digest = {

    Config.hashAlgorithm match {

      case "md5" => Hasher(data).md5
      case "sha1" => Hasher(data).sha1
      case "sha256" => Hasher(data).sha256
      case "sha384" => Hasher(data).sha384
      case "sha512" => Hasher(data).sha512
      case "bcrypt" => Hasher(data).bcrypt
      case "crc32" => Hasher(data).crc32

    }

  }

  def hexString(data: String): String = digest(data).hex

  def byteArray(data: String): Array[Byte] = digest(data).bytes

}
