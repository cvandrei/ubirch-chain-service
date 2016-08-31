package com.ubirch.chain.test.base

import com.ubirch.backend.storage.StorageCleanUp
import org.scalatest.BeforeAndAfterEach

/**
  * author: cvandrei
  * since: 2016-08-22
  */
trait ElasticSearchSpec extends UnitSpec
  with BeforeAndAfterEach
  with StorageCleanUp {

  override protected def beforeEach(): Unit = {
    resetStorage()
    Thread.sleep(100)
  }

}
