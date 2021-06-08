package com.pins.api.resolver

import com.pins.api.entities.Assets
import com.pins.api.entities.Content
import com.pins.api.entities.Pins
import graphql.GraphQLException
import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.kickstart.tools.GraphQLResolver
import jdk.nashorn.internal.runtime.regexp.joni.Config.log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.function.Supplier

@Component
class PinsQueryResolver : GraphQLQueryResolver{

    @Autowired
    lateinit var executors : ExecutorService

    fun localPins(lat : Float, lon : Float) : CompletableFuture<List<Pins>>{
        log.print("getting local pins")
        return CompletableFuture.supplyAsync(Supplier { (0..5).map { Pins(lat+it,lon+it) } },executors)
    }
    fun content(id : Float) = CompletableFuture.supplyAsync(Supplier { Content(id, " Random Text") }, executors)
}

@Component
class PinsResolver : GraphQLResolver<Pins>{
    @Autowired
    lateinit var executors : ExecutorService

    fun content(pin : Pins) = CompletableFuture.supplyAsync(Supplier { (0..5).map { Content(it.toFloat(), "Random Text" ) } }, executors)

}

@Component
class ContentResolver : GraphQLResolver<Content>{

    @Autowired
    lateinit var executors : ExecutorService

    fun assets(content: Content) = CompletableFuture.supplyAsync(Supplier { (0..5).map { Assets(0f,"Assets Url", " Assets description" ) } }, executors)
    fun pins(content: Content) = CompletableFuture.supplyAsync(Supplier { (0..5).map { Pins(it.toFloat(), it.toFloat()) } }, executors)
}

@Component
class AssetsResolver : GraphQLResolver<Assets>{
    @Autowired
    lateinit var executors : ExecutorService

    fun content(assets: Assets) = CompletableFuture.supplyAsync(Supplier { Content(0f,"Random Content") }, executors)
}