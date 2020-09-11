package com.hm.test.hello

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

val helloHandler = Handler<RoutingContext> { context ->
    context.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!")
}

fun helloRouter(vertx: Vertx): Router {
    val router = Router.router(vertx)
    router.get("/hello").handler(helloHandler)
    return router
}
