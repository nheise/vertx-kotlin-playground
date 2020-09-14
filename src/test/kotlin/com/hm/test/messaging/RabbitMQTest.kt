package com.hm.test.messaging

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.rabbitmq.RabbitMQClient
import io.vertx.rabbitmq.RabbitMQOptions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class RabbitMQTest {

    @Test
    fun messagingTest(vertx: Vertx, testContext: VertxTestContext) {

        var jsonConfig = json {
            obj(
//                "user" to "guest",
                "port" to 5672
            )
        }

//        var config = jsonConfig.mapTo(RabbitMQOptions::class.java)
        var config = RabbitMQOptions(jsonConfig)

//        assertEquals("user1", config.user)
        assertEquals(5672, config.port)

        var client = RabbitMQClient.create(vertx, config)

        client.start {

            client.confirmSelect {

                client.basicConsumer("my.queue", { rabbitMQConsumerAsyncResult ->
                    if (rabbitMQConsumerAsyncResult.succeeded()) {
                        var mqConsumer = rabbitMQConsumerAsyncResult.result()
                        mqConsumer.handler({ message ->
                            println("Got message: ${message.body()}")
                            testContext.completeNow()
                        })
                    } else {
                        rabbitMQConsumerAsyncResult.cause().printStackTrace()
                    }
                })


                var message = json {
                    obj("body" to "Hello RabbitMQ, from Vert.x !")
                }

                client.basicPublish("", "my.queue", message) { pubResult ->
                    if (pubResult.succeeded()) {
                        client.waitForConfirms { waitResult ->
                            if (waitResult.succeeded()) {
                                println("Message published !")
                            } else {
                                println(waitResult.cause().message)
                            }
                        }
                    } else {
                        pubResult.cause().printStackTrace()
                    }
                }
            }

        }
    }
}
