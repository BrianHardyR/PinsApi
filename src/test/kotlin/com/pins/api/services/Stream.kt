package com.pins.api.services

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
class Stream {
    @Test
    fun timeTest() {

        val client = WebClient.create("http://localhost:8080/stream")
        val type: ParameterizedTypeReference<ServerSentEvent<String>> =
            object : ParameterizedTypeReference<ServerSentEvent<String>>() {}

        val eventStream = client.get()
            .uri("/time")
            .retrieve()
            .bodyToFlux(type)

        eventStream.subscribe(
            { content: ServerSentEvent<String>? ->
                println(
                    content
                )
            },
            { error: Throwable? -> println(error) }
        ) { println("Complete") }
    }
}