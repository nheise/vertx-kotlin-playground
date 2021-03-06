package com.hm.test.mocking

import io.vertx.core.Vertx
import io.vertx.core.shareddata.Shareable

fun <T> shareable(sharedLocalMapName: String, sharableName: String): (vertx: Vertx, shareableBuilder: () -> T) -> T {
    return fun(vertx: Vertx, shareableBuilder: () -> T): T {

        val holders: MutableMap<String, ShareableHolder<T>> = vertx.sharedData().getLocalMap(sharedLocalMapName)

        return holders.getOrPut(sharableName) { ShareableHolder(shareableBuilder()) }.share
    }
}

class ShareableHolder<T>(val share: T) : Shareable
