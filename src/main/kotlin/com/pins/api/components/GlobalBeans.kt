package com.pins.api.components

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Component
class GlobalBeans{


    @Bean
    fun getExecutors() = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
}