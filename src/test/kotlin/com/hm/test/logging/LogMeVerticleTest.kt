package com.hm.test.logging

import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.ext.web.client.sendAwait
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class LogMeVerticleTest {

  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    runBlocking {
      vertx.deployVerticleAwait(LogMeVerticle())
      testContext.completeNow()
    }
  }

  @Test
  fun checkShareServiceOutput(vertx: Vertx, testContext: VertxTestContext) {
    runBlocking {
      val webClient = WebClient.create(vertx)

      repeatConcurrent(1) {
        var resp = webClient.get(8080, "localhost", "/logme")
          .`as`(BodyCodec.string())
          .putHeader("X_REQUEST_ID", "$it")
          .sendAwait()
        assertEquals(200, resp.statusCode())
      }

      testContext.completeNow()
    }
  }

  private suspend fun repeatConcurrent(steps: Int, action: suspend (iteration: Int) -> Unit) {
    coroutineScope {
      (1..steps).forEach {
        launch {
          action(it)
        }
      }
    }
  }

}
