package com.ubirch.chain.backend.util

import com.roundeights.hasher.Hash

/**
  * author: cvandrei
  * since: 2016-07-29
  */
object BlockUtil {

  def size(hashes: Seq[String]): Long = hashes.map(Hash(_).bytes.length.toLong).sum

}
