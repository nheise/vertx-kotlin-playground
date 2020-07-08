package com.hm.test.mocking

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class VerticleWithSharedServiceTest {

  @MockK
  private var mockService = mockk<SharedService>()

  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {

    vertx.deployVerticle(VerticleWithSharedService(), testContext.succeeding<String> { _ -> testContext.completeNow() })
  }

  @Test
  fun checkShareServiceOutput(vertx: Vertx, testContext: VertxTestContext) {
    val webClient = WebClient.create(vertx)
    webClient.get(8888, "localhost", "/")
      .`as`(BodyCodec.string())
      .send(testContext.succeeding { resp ->
        testContext.verify {
          assertEquals(200, resp.statusCode())
          assertEquals("something", resp.body())
          testContext.completeNow()
        }
      })
  }

  @Test
  fun checkMockedShareServiceOutput(vertx: Vertx, testContext: VertxTestContext) {

    sharedService(vertx, mockService)
    every { mockService.giveMeSomething() } returns "some mock thing"

    val webClient = WebClient.create(vertx)
    webClient.get(8888, "localhost", "/")
      .`as`(BodyCodec.string())
      .send(testContext.succeeding { resp ->
        testContext.verify {
          assertEquals(200, resp.statusCode())
          assertEquals("some mock thing", resp.body())
          testContext.completeNow()
        }
      })
  }
}
