package com.hm.test

import com.hm.test.hello.helloRouter
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    val router = Router.router(vertx)

    router.mountSubRouter("/", helloRouter(vertx))

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
