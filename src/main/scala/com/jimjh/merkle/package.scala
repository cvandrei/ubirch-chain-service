package com.jimjh

/** Provides an implementation of Merkle Trees.
  *
  * @author Jim Lim - jim@jimjh.com
  * @see http://en.wikipedia.org/wiki/Merkle_tree
  */
package object merkle {

  /** Data blocks are byte sequences. */
  type Block = Seq[Byte]

  /** Hash functions map blocks to blocks */
  type Digest = (Block) => Block

  private[merkle] def notNull(x: Any, n: String) =
    require(null != x, s"`$n` must not be null.")
}
