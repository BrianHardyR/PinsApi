package com.pins.api.controller

import com.pins.api.entities.Account
import com.pins.api.entities.AccountType
import com.pins.api.security.AppUserDetails
import com.pins.api.services.AccountCreationRequest
import com.pins.api.services.AuthenticationService
import com.pins.api.services.EmailPasswordAuthRequest
import com.pins.api.services.SwitchAccountRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController {

    @Autowired
    lateinit var authenticationService: AuthenticationService


    @PostMapping("/login")
    fun login( @RequestBody emailPasswordAuthRequest: EmailPasswordAuthRequest): ResponseEntity<Map<String,Any>> {
        println("login endpoint")
        val userDetails = authenticationService.loginAndAssignToken(emailPasswordAuthRequest)
        val token = authenticationService.getToken(userDetails)
        return ResponseEntity.ok(
            mapOf(
                ("token" to token),
                ("auth" to userDetails)
            )
        )

    }

    @PostMapping("/register")
    fun register( @RequestBody creationRequest: AccountCreationRequest ):ResponseEntity<Account>{
        return ResponseEntity.ok(authenticationService.createAccount(creationRequest,AccountType.DEFAULT))
    }

    @PostMapping("/switch")
    fun switchAccount(@RequestBody switchAccountRequest: SwitchAccountRequest) : ResponseEntity<Map<String,Any>>{
        val userDetails = authenticationService.switchAccount(switchAccountRequest)
        val token = authenticationService.getToken(userDetails)
        return ResponseEntity.ok(
            mapOf(
                ("token" to token),
                ("auth" to userDetails)
            )
        )
    }

    @GetMapping("/details")
    fun getLoggedInUserDetails():ResponseEntity<AppUserDetails>{
        println("getting user data")
        return ResponseEntity.ok(authenticationService.getDataFromAccount())
    }

}