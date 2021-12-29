package com.pins.api.components

import com.pins.api.security.JwtTokenFilter
import com.pins.api.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class GlobalBeans{

    @Autowired
    lateinit var jwtTokenFilter: JwtTokenFilter
    @Autowired
    lateinit var authService: AuthService

    @Bean
    fun passwordEncoder() : PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun getExecutors() = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    @Bean
    fun getAuthenticationService() = authService
}