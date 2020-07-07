package com.hm.test.search

import com.hm.test.MainVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class SearchTest {

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { _ -> testContext.completeNow() })
  }

  @Test
  @DisplayName("When everything is all right, return status 200")
  fun checkSearchQuery(vertx: Vertx, testContext: VertxTestContext) {
    val webClient = WebClient.create(vertx)
    webClient.get(8888, "localhost", "/search?query=foo")
      .`as`(BodyCodec.string())
      .send(testContext.succeeding { resp ->
        testContext.verify {
          assert(resp.statusCode() == 200)
          assert(resp.body() == "bar")
          testContext.completeNow()
        }
      })
  }

  @Test
  @DisplayName("When query not fit, return status 400")
  fun checkFalseSearchQuery(vertx: Vertx, testContext: VertxTestContext) {
    val webClient = WebClient.create(vertx)
    webClient.get(8888, "localhost", "/search?query=fooo")
      .`as`(BodyCodec.string())
      .send(testContext.succeeding { resp ->
        testContext.verify {
          assert(resp.statusCode() == 400)
          testContext.completeNow()
        }
      })
  }
}
