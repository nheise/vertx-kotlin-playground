package com.hm.test.mocking

import io.vertx.core.Vertx
import io.vertx.core.shareddata.Shareable

const val SHARED_SERVICE_LOCAL_MAP_NAME = "__vertx.SharedService"
const val DEFAULT_SERVICE_NAME = "SharedServiceName"

fun sharedService(service: SharedService = SharedService()) = service
fun sharedService(vertx: Vertx, service: SharedService = SharedService()): SharedService {

  val holders: MutableMap<String, SharedServiceHolder> = vertx.sharedData().getLocalMap(SHARED_SERVICE_LOCAL_MAP_NAME)

  return holders.getOrPut(DEFAULT_SERVICE_NAME) { SharedServiceHolder(service) }.service
}

class SharedServiceHolder(service: SharedService) : Shareable {
  val service = service
}

class SharedService {
  fun giveMeSomething() = "something"
}
