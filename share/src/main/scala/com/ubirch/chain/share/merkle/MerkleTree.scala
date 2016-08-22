package com.ubirch.chain.share.merkle

import com.ubirch.util.crypto.hash.HashUtil

/**
  * Based on: https://github.com/ErikBjare/merkle
  *
  * author: cvandrei
  * since: 2016-08-02
  */

class MerkleTree(var root: Node)

trait Node {

  def hash(): String

  def string(depth: Integer = 0, childrenBefore: Integer = 0): String = {
    val nodeType = this match {
      case x: Branch => "branch"
      case x: Leaf => "leaf"
      // TODO treat Hash as leaf?
    }

    // │ ├ ─ └ ┌
    val treeRoot = s"${if (depth == 0) "┌" else ""}${"│" * (depth - 1)}${if (depth > 0) if (nodeType.equals("branch")) "├┬" else if (childrenBefore == 1) "└" else "├" else ""}"
    val treeArm = s"${"─" * (depth * 1 + 1)}"
    val label = s"$nodeType\t${hash()}"
    treeRoot + treeArm + label
  }

}

class Branch(private val childs: List[Node]) extends Node {

  def children(): List[Node] = {
    childs
  }

  def addChild(child: Node): Unit = {
    childs :+ child
  }

  override def hash(): String = {

    val hashedChildren: List[String] = childs.map((n: Node) => n.hash())

    childs.size match {

      case 1 => hashedChildren.head

      case _ =>
        val concatenatedHashes: String = hashedChildren.mkString
        MerkleTree.computeHash(concatenatedHashes)

    }

  }

}

object Branch {

  def apply(children: Node*): Branch = {
    new Branch(children.toList)
  }

  def createFromHashes(hashStrings: Seq[String]): Branch = {
    val hashes = hashStrings.map(HashNode(_))
    createFromNodes(hashes)
  }

  def createFromLeaves(dataStrings: Seq[String]): Branch = {
    val leaves = dataStrings.map(Leaf(_))
    createFromNodes(leaves)
  }

  def createFromNodes(nodes: Seq[Node]): Branch = {
    var branches = nodesToBranches(nodes)
    while (branches.length > 1) {
      branches = nodesToBranches(branches)
    }
    branches.head
  }

  def nodesToBranches(leaves: Seq[Node]): Seq[Branch] = {
    leaves.grouped(2).map { group =>
      group.length match {
        case 1 => Branch(group.head)
        case 2 => Branch(group.head, group.tail.head)
      }
    }.toSeq
  }

}

class Leaf(data: String) extends Node {

  def get(): String = data

  override def hash(): String = {
    MerkleTree.computeHash(get())
  }

}

object Leaf {
  def apply(data: String): Leaf = {
    new Leaf(data)
  }
}

/**
  * In a [HashNode] the data field contains a hash.
  *
  * @param data a hash
  */
class HashNode(data: String) extends Node {

  def get(): String = data.toLowerCase

  override def hash(): String = get()

}

object HashNode {
  def apply(data: String): HashNode = new HashNode(data)
}

object MerkleTree {
  def computeHash(data: String): String = HashUtil.sha256Digest(data)
}
