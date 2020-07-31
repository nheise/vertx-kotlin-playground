package com.hm.test.mocking

import io.vertx.core.Vertx

const val SHARED_SERVICE_LOCAL_MAP_NAME = "__vertx.SharedService"
const val DEFAULT_SERVICE_NAME = "SharedServiceName"

val getShared = shareable<SharedService>(SHARED_SERVICE_LOCAL_MAP_NAME, DEFAULT_SERVICE_NAME)

fun sharedService(service: SharedService = SharedService()) = service
fun sharedService(vertx: Vertx, service: SharedService = SharedService()) = getShared(vertx) { service }

class SharedService {
  fun giveMeSomething() = "something"
}
