package com.hm.test.auth.keycloak

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.auth.oauth2.oAuth2ClientOptionsOf
import io.vertx.kotlin.ext.auth.oauth2.providers.KeycloakAuth
import io.vertx.kotlin.ext.web.client.sendAwait
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class KeycloakAuthTest {

  private val token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJqUm9USnkxYzZsWXhhWldlQnV5alFIUnFVdWU0U2lXVmozN1JsT0pWNGlzIn0.eyJleHAiOjE1OTg0MzQ4ODMsImlhdCI6MTU5ODQzNDU4MywianRpIjoiN2Y3YjUyOTgtMWM4My00NTYwLTk3Y2EtMDYyYzhlYTEzYTY4IiwiaXNzIjoiaHR0cHM6Ly9kZXYuYXV0aC5tZWluZS1nZXN1bmRoZWl0c3BsYXR0Zm9ybS5kZS9hdXRoL3JlYWxtcy9tYXN0ZXIiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZGMzZGVkYzQtMzVmOC00MWIwLTg2ODItMmExOWZhOTk4ZWNkIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidmhvZW5peCIsInNlc3Npb25fc3RhdGUiOiIwZjdhZTIxMC1lMTM0LTRhNzgtYThlMC0yNGNiODkwNWZmOTciLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInJlZ2lzdGVyZWQiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6ImExMjM0NTY3ODkifQ.PYLZ1OUcDPYN_a9KTb3cVB0kKdjP7WAO4a-DDRfN0G7i_zOEX5YcKO_D_LXdiekEmOuqv2zjpF9q6LvkR8eZaK5R6kNxYSdAZqcsWXKc_g-DUwXDN9eO1_kZrGH_BBBNfkOwvKy5eBVrbwUJkSG6gasHiAodDQwTfr40lF81GvIEo0HJv0nQnVkso6ItrSRrQzBxb5OG0XIRGRB083rNMLJ_pVZj0bkSmxzdxlEL6qGtwQjFQQZymQXUZtsfHRRt895UmiB645RuvV47qdpWd17BqtjVoCQn7SudI1Fyq1gC0OL8_uyyXKUBiPy11MFRCZchqwXldSU0fhG0o3R3vA"

  private val webClientOptions = webClientOptionsOf(
    defaultHost = "localhost",
    defaultPort = 8080
  )

  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    runBlocking {
      vertx.deployVerticleAwait(KeycloakAuthTestVerticle())
      testContext.completeNow()
    }
  }

  @Test
  fun checkHello(vertx: Vertx, testContext: VertxTestContext) {
    runBlocking {
      WebClient.create(vertx, webClientOptions)
        .get("/hello")
        .sendAwait()
        .bodyAsString()
        .also {
          assertEquals("hello", it)
          testContext.completeNow()
        }
    }
  }

  @Test
  fun checkKeycloak(vertx: Vertx, testContext: VertxTestContext) {
    runBlocking {
      KeycloakAuth.discoverAwait(
        vertx,
        oAuth2ClientOptionsOf(
          site = "https://dev.auth.meine-gesundheitsplattform.de/auth/realms/master",
          clientID = "public-api",
          clientSecret = "f60e5d7a-edaf-4d2b-ad7f-e5166e5f7ed8"
        )
      )
        .let {
          it.introspectToken(token) {
            if (it.failed()) println(it.cause())
            assertEquals(true, it.succeeded())
          }
        }
      testContext.completeNow()
    }
  }
}

class KeycloakAuthTestVerticle : CoroutineVerticle() {
  override suspend fun start() {
    Router.router(vertx)
      .also(::sayHello)
      .let {
        vertx.createHttpServer().requestHandler(it).listenAwait(8080)
      }
  }
}

private suspend fun authHandler(vertx: Vertx) {
  KeycloakAuth.discoverAwait(
    vertx,
    oAuth2ClientOptionsOf(
      clientID = "",
      clientSecret = "",
      site = "http://dev.auth.meine-gesundheitsplattform.de/auth/realms/master")
  )
}

private fun sayHello(router: Router) {
  router.get("/hello").handler {
    it.response().setStatusCode(200).end("hello")
  }
}
