package com.pins.api.controller

import com.pins.api.repo.PostsRepo
import com.pins.api.utils.now
import com.pins.api.utils.toLong
import org.neo4j.driver.internal.shaded.reactor.core.publisher.Flux
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/stream")
class StreamController {

    @Autowired
    lateinit var postsRepo: PostsRepo

    @GetMapping("/time")
    fun serverTime(): Flux<ServerSentEvent<String>> {
        return Flux.interval(Duration.ofSeconds(1))
            .map {
                ServerSentEvent.builder<String>()
                    .id("$it")
                    .event("periodic-event")
                    .data("${now()}".also { println(it) })
                    .build()
            }
    }

    @GetMapping("/posts")
    fun posts(): Flux<ServerSentEvent<Int>> {
        println("Post Stream")
        return Flux.interval(Duration.ofSeconds(1))
            .map { postsRepo.findAll() }
            .distinctUntilChanged()
            .map {
                ServerSentEvent.builder<Int>()
                    .id("${now().toLong()}")
                    .event("new posts")
                    .data(it.size)
                    .build()
            }
    }


}
