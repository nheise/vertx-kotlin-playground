package com.hm.test.vertx.yaml

import io.vertx.config.ConfigRetriever
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.config.configRetrieverOptionsOf
import io.vertx.kotlin.config.configStoreOptionsOf
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestYamlConfig {

  @Test
  fun loadConfiguration(vertx: Vertx, testContext: VertxTestContext) {

    val configOptions = configStoreOptionsOf(
      type = "file",
      format = "yaml",
      config = json {
        obj("path" to "src/test/resources/test-config.yaml")
      })

    val options = configRetrieverOptionsOf(
      stores = listOf(configOptions))

    val retriever = ConfigRetriever.create(vertx, options)

    retriever.getConfig {
      val config = it.result()

    }

    testContext.completeNow()
  }

}
