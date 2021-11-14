package com.pins.api.controller

import com.pins.api.request_response.auth.AuthRequest
import com.pins.api.request_response.auth.RegistrationRequest
import com.pins.api.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.security.RolesAllowed


@RestController
@RequestMapping("/auth")
class AuthController {

    @Autowired
    lateinit var authService: AuthService


    @PostMapping("/login")
    @RolesAllowed()
    fun login(@RequestBody request: AuthRequest) : ResponseEntity<*>{
        return ResponseEntity.ok(authService.login(request))
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegistrationRequest): ResponseEntity<*> {
        return ResponseEntity.ok(authService.register(request))
    }
}
