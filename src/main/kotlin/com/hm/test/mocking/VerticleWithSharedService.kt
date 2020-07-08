package com.hm.test.mocking

import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle

class VerticleWithSharedService : CoroutineVerticle() {

  override fun start(startPromise: Promise<Void>) {

    val router = Router.router(vertx)

    router.get("/").handler(giveMeSomethingHandler)

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

val giveMeSomethingHandler = Handler<RoutingContext> {
  val service = sharedService(it.vertx())
  it.response()
    .putHeader("content-type", "text/plain")
    .end(service.giveMeSomething())
}
