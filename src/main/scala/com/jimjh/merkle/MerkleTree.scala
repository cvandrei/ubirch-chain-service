package com.jimjh.merkle

import java.util.Base64

import com.ubirch.chain.backend.util.HashUtil

/** Hash of hashes, using a binary tree.
  *
  * The hash at each node is calculated using
  * {{{
  *   hash(hash of left + hash of right)
  * }}}
  *
  * @constructor creates a new immutable merkle tree
  * @param hash hash of children, or data block
  * @author Jim Lim - jim@quixey.com (original source copied from https://github.com/jimjh/merkle-trees-impl)
  * @author cvandrei - allow handling of unbalanced trees (e.g. only three leafs)
  */
class MerkleTree(val hash: Block,
                 val left: Option[MerkleTree] = Option.empty,
                 val right: Option[MerkleTree] = Option.empty) {

  def this(hash: Block, left: MerkleTree, right: MerkleTree) =
    this(hash, Option(left), Option(right))

  def hashHex: String = HashUtil.convertToHex(hash.toArray)

  override def toString =
    s"MerkleTree(hash=${Base64.getEncoder.encodeToString(hash.toArray)}"
}

/** Companion object for [[MerkleTree]], acting as a factory. */
object MerkleTree {

  def apply(data: Seq[Block], digest: Digest): MerkleTree = {
    var trees = data.map { block => new MerkleTree(digest(block)) }
    while (trees.length > 1) {
      trees = trees.grouped(2).map(make(digest, _)).toSeq
    }
    trees.head
  }

  private[this] def make(digest: Digest, arr: Seq[MerkleTree]): MerkleTree = {
    val fixedLength = arr.length match {
      case 2 => arr
      case 1 => arr :+ arr.head
    }
    new MerkleTree(merge(digest, fixedLength), fixedLength.head, fixedLength(1))
  }

  private[this] def merge(digest: Digest, arr: Seq[MerkleTree]): Block = {
    digest(arr.head.hash ++ arr(1).hash)
  }

}