package com.hm.test.logging

import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.slf4j.MDCContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC

class LogMeVerticle : CoroutineVerticle() {

  private val logger = LoggerFactory.getLogger(LogMeVerticle::class.java)

  override suspend fun start() {
    val router = Router.router(vertx)

    router.get("/logme")
      .handler(::addRequestIdIntoLogContext)
      .handler {
        AppLog.info("log in normal handler")
        it.next()
      }
      .coroutineHandler {
        AppLog.info("log in coroutine handler")
        it.next()
      }
      .handler {
        it.response().end()
      }
      .failureHandler {
        logger.error("Unexpected error", it.failure())
        it.next()
      }
      .handler(::clearLogContext)

    vertx
      .createHttpServer()
      .requestHandler(router)
      .listenAwait(8080, "localhost")
  }
}

object AppLog : Logger by LoggerFactory.getLogger("Application Logger")

fun addRequestIdIntoLogContext(ctx: RoutingContext) {
  MDC.put("requestId", ctx.requestId)
  ctx.next()
}

fun clearLogContext(ctx: RoutingContext) {
  MDC.clear()
  println("Clear log context")
}

val RoutingContext.requestId: String
  get() = request().getHeader("X_REQUEST_ID") ?: "no_request_id_present"

fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit): Route {
  handler { ctx ->
    CoroutineScope(ctx.vertx().dispatcher().plus(MDCContext())).launch {
      try {
        fn(ctx)
      } catch (e: Exception) {
        ctx.fail(e)
      }
    }
  }
  return this
}
