package com.pins.api.security

import com.pins.api.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import javax.servlet.http.HttpServletResponse


@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var authService: AuthService

    @Autowired
    lateinit var jwtTokenFilter: JwtTokenFilter

    override fun configure(http: HttpSecurity?) {
        println("Configure HttpSecurity")
        http?.cors()
            ?.and()
            ?.csrf()?.disable()
            ?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ?.and()?.exceptionHandling()?.authenticationEntryPoint { request, response, authException ->
                response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    authException.message
                )
            }?.and()
            ?.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            ?.authorizeRequests()
            ?.antMatchers("/")?.permitAll()
            ?.antMatchers("/auth/login")?.permitAll()
            ?.antMatchers("/auth/googleLogin")?.permitAll()
            ?.antMatchers("/auth/register")?.permitAll()
            ?.antMatchers("/stream/**")?.permitAll()
            ?.anyRequest()?.authenticated()
    }


    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth ?: return
        auth.userDetailsService(authService)
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun corsFilter(): CorsFilter? {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }


    @Bean
    fun grantedAuthorityDefaults(): GrantedAuthorityDefaults? {
        return GrantedAuthorityDefaults("") // Remove the ROLE_ prefix
    }

}