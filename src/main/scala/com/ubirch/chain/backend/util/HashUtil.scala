package com.ubirch.chain.backend.util

import com.jimjh.merkle.Block
import com.roundeights.hasher.{Digest, Hash, Hasher}
import com.ubirch.chain.backend.config.Config

/**
  * author: cvandrei
  * since: 2016-07-28
  */
object HashUtil {

  def digest(data: Array[Byte]): Digest = {

    val hasher = Hasher(data)
    Config.hashAlgorithm match {

      case "md5" => hasher.md5
      case "sha1" => hasher.sha1
      case "sha256" => hasher.sha256
      case "sha384" => hasher.sha384
      case "sha512" => hasher.sha512
      case "bcrypt" => hasher.bcrypt
      case "crc32" => hasher.crc32

    }

  }

  def digest(data: Block): Block = digest(data.toArray).bytes.toSeq

  def digest(data: String): Digest = digest(data.getBytes)

  def hexString(data: String): String = digest(data).hex

  def byteArray(data: String): Array[Byte] = digest(data).bytes

  def toHex(buf: Array[Byte]): String = Hash(buf).hex

}
