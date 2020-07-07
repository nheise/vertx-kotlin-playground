package com.hm.test.hello

import com.hm.test.MainVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class HelloTest {

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { _ -> testContext.completeNow() })
  }

  @Test
  fun verticle_deployed(vertx: Vertx, testContext: VertxTestContext) {
    val webClient = WebClient.create(vertx)
    webClient.get(8888, "localhost", "/hello")
      .`as`(BodyCodec.string())
      .send(testContext.succeeding { resp ->
        testContext.verify {
          assert(resp.statusCode() == 200)
          assert(resp.body() == "Hello from Vert.x!")
          testContext.completeNow()
        }
      })
  }

}
