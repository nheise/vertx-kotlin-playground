package com.hm.test

import com.hm.test.hello.helloRouter
import com.hm.test.search.searchRouter
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle

class MainVerticle : CoroutineVerticle() {

  override fun start(startPromise: Promise<Void>) {
    val router = Router.router(vertx)

    router.mountSubRouter("/", helloRouter(vertx))
    router.mountSubRouter("/", searchRouter(vertx))

    vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8888) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          println("HTTP server started on port 8888")
        } else {
          startPromise.fail(http.cause());
        }
      }
  }
}
