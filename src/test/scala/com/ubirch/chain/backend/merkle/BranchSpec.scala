package com.ubirch.chain.backend.merkle

import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2016-08-02
  */
class BranchSpec extends FeatureSpec
  with Matchers {

  feature("Branch.apply") {

    scenario("unbalanced tree with one leaf") {

      val leaf = Leaf("ubirch-a1")

      val branch = Branch(leaf)

      branch.hash should be("6f00087163d806b524751bb7c69cfc2175a0f37a9db88e050f69f6f254673b27")

    }

    scenario("balanced tree with two leaves") {

      val leaf1 = Leaf("ubirch-a1")
      val leaf2 = Leaf("ubirch-a2")

      val branch = Branch(leaf1, leaf2)

      branch.hash should be("2470e7c3d5dc671cef2e2d3d8bac945638d6923b3bb666f15af345058cb21ae9")

    }

    scenario("unbalanced tree with three leaves") {

      val leaf1 = Leaf("ubirch-a1")
      val leaf2 = Leaf("ubirch-a2")
      val leaf3 = Leaf("ubirch-a3")

      val branch1 = Branch(leaf1, leaf2)
      val branch2 = Branch(leaf3)

      val branchA = Branch(branch1, branch2)

      branchA.hash should be("bad200bf6a0e2cd2bb68d9c47e652ab0d74143defeaf7f2c573570fb2710564a")

    }

    scenario("balanced tree with four leaves") {

      val leaf1 = Leaf("ubirch-a1")
      val leaf2 = Leaf("ubirch-a2")
      val leaf3 = Leaf("ubirch-a3")
      val leaf4 = Leaf("ubirch-a4")

      val branch1 = Branch(leaf1, leaf2)
      val branch2 = Branch(leaf3, leaf4)

      val branchA = Branch(branch1, branch2)

      branchA.hash should be("3c7b55ad06a2443f3d2c8422e98f26dbbaf067ec85e6795fc3c9ab2628f45f6e")

    }

  }

  feature("Branch.createFromHashes") {

    scenario("balanced tree with one leaf") {

      val nodes = Seq("6f00087163d806b524751bb7c69cfc2175a0f37a9db88e050f69f6f254673b27")

      val branch = Branch.createFromHashes(nodes)
      branch.hash should be("6f00087163d806b524751bb7c69cfc2175a0f37a9db88e050f69f6f254673b27")

    }

    scenario("balanced tree with two leaves") {

      val nodes = Seq(
        "6f00087163d806b524751bb7c69cfc2175a0f37a9db88e050f69f6f254673b27",
        "13ee2a02a25f9c5731e05d13b5831df60020736f198393ad784fbf2ea29d6a54"
      )

      val branch = Branch.createFromHashes(nodes)
      branch.hash should be("2470e7c3d5dc671cef2e2d3d8bac945638d6923b3bb666f15af345058cb21ae9")

    }

    scenario("balanced tree with four leaves") {

      val nodes = Seq(
        "6f00087163d806b524751bb7c69cfc2175a0f37a9db88e050f69f6f254673b27",
        "13ee2a02a25f9c5731e05d13b5831df60020736f198393ad784fbf2ea29d6a54",
        "64ad5cbea4d8bdf18eb8122a5c760a724f3fafd6b46e2f2f1ed73a89cd1e5fce",
        "f128899c7209d43cc691e3f164401fe3fd39d4989482b1e1996062e85418864d"
        )

      val branch = Branch.createFromHashes(nodes)

      branch.hash should be("3c7b55ad06a2443f3d2c8422e98f26dbbaf067ec85e6795fc3c9ab2628f45f6e")

    }

  }

  feature("Branch.createFromNodes") {

    scenario("balanced tree with one leaf") {

      val leaf1 = Leaf("ubirch-a1")

      val nodes = Seq(leaf1)

      val branch = Branch.createFromNodes(nodes)
      branch.hash should be("6f00087163d806b524751bb7c69cfc2175a0f37a9db88e050f69f6f254673b27")

    }

    scenario("balanced tree with two leaves") {

      val leaf1 = Leaf("ubirch-a1")
      val leaf2 = Leaf("ubirch-a2")

      val nodes = Seq(leaf1, leaf2)

      val branch = Branch.createFromNodes(nodes)
      branch.hash should be("2470e7c3d5dc671cef2e2d3d8bac945638d6923b3bb666f15af345058cb21ae9")

    }

    scenario("balanced tree with four leaves") {

      val leaf1 = Leaf("ubirch-a1")
      val leaf2 = Leaf("ubirch-a2")
      val leaf3 = Leaf("ubirch-a3")
      val leaf4 = Leaf("ubirch-a4")

      val nodes = Seq(leaf1, leaf2, leaf3, leaf4)

      val branch = Branch.createFromNodes(nodes)

      branch.hash should be("3c7b55ad06a2443f3d2c8422e98f26dbbaf067ec85e6795fc3c9ab2628f45f6e")

    }

  }

  feature("Branch.nodesToBranches") {

    scenario("balanced tree with one leaf") {

      val leaf1 = Leaf("ubirch-a1")
      val branch = Branch(leaf1)

      val nodes = Seq(leaf1)

      val branches = Branch.nodesToBranches(nodes)
      branches.length should be(1)
      branches.head.children() should equal(branch.children())
      branches.head.hash should be("6f00087163d806b524751bb7c69cfc2175a0f37a9db88e050f69f6f254673b27")

    }

    scenario("balanced tree with two leaves") {

      val leaf1 = Leaf("ubirch-a1")
      val leaf2 = Leaf("ubirch-a2")
      val branch = Branch(leaf1, leaf2)

      val nodes = Seq(leaf1, leaf2)

      val branches = Branch.nodesToBranches(nodes)
      branches.length should be(1)
      branches.head.children() should equal(branch.children())
      branches.head.hash should be("2470e7c3d5dc671cef2e2d3d8bac945638d6923b3bb666f15af345058cb21ae9")

    }

    scenario("balanced tree with three leaves") {

      val leaf1 = Leaf("ubirch-a1")
      val leaf2 = Leaf("ubirch-a2")
      val leaf3 = Leaf("ubirch-a3")
      val branch1 = Branch(leaf1, leaf2)
      val branch2 = Branch(leaf3)

      val nodes = Seq(leaf1, leaf2, leaf3)

      val branches = Branch.nodesToBranches(nodes)

      branches.length should be(2)

      branches.head.children() should equal(branch1.children())
      branches.head.hash should be("2470e7c3d5dc671cef2e2d3d8bac945638d6923b3bb666f15af345058cb21ae9")

      branches(1).children() should equal(branch2.children())
      branches(1).hash should be("64ad5cbea4d8bdf18eb8122a5c760a724f3fafd6b46e2f2f1ed73a89cd1e5fce")

    }

    scenario("balanced tree with four leaves") {

      val leaf1 = Leaf("ubirch-a1")
      val leaf2 = Leaf("ubirch-a2")
      val leaf3 = Leaf("ubirch-a3")
      val leaf4 = Leaf("ubirch-a4")
      val branch1 = Branch(leaf1, leaf2)
      val branch2 = Branch(leaf3, leaf4)

      val nodes = Seq(leaf1, leaf2, leaf3, leaf4)

      val branches = Branch.nodesToBranches(nodes)

      branches.length should be(2)

      branches.head.children() should equal(branch1.children())
      branches.head.hash should be("2470e7c3d5dc671cef2e2d3d8bac945638d6923b3bb666f15af345058cb21ae9")

      branches(1).children() should equal(branch2.children())
      branches(1).hash should be("c450037ec1d64bbfe0a70230a2af7f1baa60fff610b763ec831316bcbdbe4a39")

    }

  }

}
