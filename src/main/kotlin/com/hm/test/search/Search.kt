package com.hm.test.search

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.core.streams.end

const val QUERY_PARAM_NAME = "query"
const val CTX_QUERY_NAME = "query_ctx"

val validationHandler = HTTPRequestValidationHandler.create()
    .addQueryParamWithPattern(QUERY_PARAM_NAME, "foo", true)

val extractQueryParam = Handler<RoutingContext> {
    it.put(CTX_QUERY_NAME, it.queryParam(QUERY_PARAM_NAME).first())
    it.next()
}

val showQuery = Handler<RoutingContext> {
    println(it.get<String>(CTX_QUERY_NAME))
    it.next()
}

val searchHandler = Handler<RoutingContext> {
    it.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(200)
        .end(false) { json { obj("foo" to "bar") } }
}

val failureHandler = Handler<RoutingContext> {
    println(it.failure())
    it.response().setStatusCode(400).end()
}

fun searchRouter(vertx: Vertx): Router {
    val router = Router.router(vertx)

//  router.route().handler(BodyHandler.create())

    router.get("/search")
        .handler(validationHandler)
        .handler(extractQueryParam)
        .handler(showQuery)
        .handler(searchHandler)
        .failureHandler(failureHandler)
    return router
}
