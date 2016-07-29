package com.jimjh.merkle

import java.nio.ByteBuffer
import java.util.zip.CRC32

import org.scalatest.{FeatureSpec, Matchers}

/** Specs for [[com.jimjh.merkle.MerkleTree]].
  *
  * @author Jim Lim - jim@quixey.com
  */
class MerkleTreeSpec extends FeatureSpec
  with Matchers {

  feature("hashing a tree") {

    scenario("where two (same tree twice) have same top hash") {
      val data = Seq(
        Seq[Byte](1, 2, 3),
        Seq[Byte](4, 5, 6),
        Seq[Byte](127, -128, -127),
        Seq[Byte](117, 116, 118),
        Seq[Byte](95, 94, 93),
        Seq[Byte](125, 124, 123),
        Seq[Byte](85, 84, 83),
        Seq[Byte](75, 74, 73)
      )
      val tree1 = MerkleTree(data, crc32)
      val tree2 = MerkleTree(data, crc32)
      tree1.hash should be(tree2.hash)
    }

    scenario("where two have a different top hash") {
      val data1 = Seq(
        Seq[Byte](1, 2, 3),
        Seq[Byte](4, 5, 6),
        Seq[Byte](127, -128, -127),
        Seq[Byte](117, 116, 118),
        Seq[Byte](95, 94, 93),
        Seq[Byte](125, 124, 123),
        Seq[Byte](85, 84, 83),
        Seq[Byte](75, 74, 73)
      )

      val data2 = Seq(
        Seq[Byte](61, 2, 3),
        Seq[Byte](64, 5, 6),
        Seq[Byte](27, -128, -127),
        Seq[Byte](17, 116, 118),
        Seq[Byte](95, 94, 93),
        Seq[Byte](125, 124, 123),
        Seq[Byte](85, 84, 83),
        Seq[Byte](75, 74, 73)
      )

      val tree1 = MerkleTree(data1, crc32)
      val tree2 = MerkleTree(data2, crc32)
      tree1.hash should not be tree2.hash
    }

    scenario("where two slightly different trees have the same child hash") {
      val data1 = Seq(
        Seq[Byte](1, 2, 3),
        Seq[Byte](4, 5, 6),
        Seq[Byte](127, -128, -127),
        Seq[Byte](117, 116, 118),
        Seq[Byte](95, 94, 93),
        Seq[Byte](125, 124, 123),
        Seq[Byte](85, 84, 83),
        Seq[Byte](75, 74, 73)
      )

      val data2 = Seq(
        Seq[Byte](61, 2, 3),
        Seq[Byte](64, 5, 6),
        Seq[Byte](27, -128, -127),
        Seq[Byte](17, 116, 118),
        Seq[Byte](95, 94, 93),
        Seq[Byte](125, 124, 123),
        Seq[Byte](85, 84, 83),
        Seq[Byte](75, 74, 73)
      )

      val tree1 = MerkleTree(data1, crc32)
      val tree2 = MerkleTree(data2, crc32)
      tree1.right.get.hash should be(tree2.right.get.hash)
      tree1.left.get.hash should not be tree2.left.get.hash
    }

    scenario("where tree has 5 leafs") {

      val data = Seq(
        Seq[Byte](1, 2, 3),
        Seq[Byte](4, 5, 6),
        Seq[Byte](127, -128, -127),
        Seq[Byte](117, 116, 118),
        Seq[Byte](95, 94, 93)
      )

      val tree = MerkleTree(data, crc32)
      tree.hashHex should be("00000000bdb95d54")

    }

  }

  def crc32(b: Block) = {
    val digest = new CRC32()
    digest.update(b.toArray)

    val buffer = ByteBuffer.allocate(8)
    buffer.putLong(digest.getValue)
    buffer.array
  }
}
