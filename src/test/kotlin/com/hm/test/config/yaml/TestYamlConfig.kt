package com.hm.test.config.yaml

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.config.ConfigRetriever
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.config.configRetrieverOptionsOf
import io.vertx.kotlin.config.configStoreOptionsOf
import io.vertx.kotlin.core.json.get
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

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
      val config = it.result().getJsonObject("level1").getJsonObject("props")
      //println(config)

      val props = Properties()
      props.putAll(config.map)

      assertEquals("value1", props.getProperty("key1"))
      testContext.completeNow()
    }
  }

  @Test
  fun loadConfigurationAsObject(vertx: Vertx, testContext: VertxTestContext) {

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
      val config: JsonObject = it.result()
      val propsConfig = config.get<JsonObject>("level1").get<JsonObject>("props").mapTo(PropsConfig::class.java)

      assertEquals("value1", propsConfig.key1)

      testContext.completeNow()
    }
  }

  data class PropsConfig(
    @JsonProperty("key1") val key1: String,
    @JsonProperty("key2") val key2: String
  )
}
