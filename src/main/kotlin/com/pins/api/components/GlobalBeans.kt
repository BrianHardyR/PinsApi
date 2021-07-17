package com.pins.api.components

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class GlobalBeans{

    @Bean
    fun passwordEncoder() : PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun getExecutors() = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
}