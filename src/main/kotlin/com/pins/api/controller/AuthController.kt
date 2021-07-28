package com.pins.api.controller

import com.pins.api.entities.*
import com.pins.api.security.AppUserDetails
import com.pins.api.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController {

    @Autowired
    lateinit var authenticationService: AuthenticationService


    @PostMapping("/login")
    fun login(@RequestBody emailPasswordAuthRequest: EmailPasswordAuthRequest): ResponseEntity<Map<String, Any>> {
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

    @PostMapping("/googleLogin")
    fun login(@RequestBody googleAuthRequest: GoogleAuthRequest): ResponseEntity<Map<String, Any>> {
        println("google login")

        val userDetails = authenticationService.signInWithGoogle(googleAuthRequest.authKey, googleAuthRequest.googleId)
        println("\nget token google\n")
        val token = authenticationService.getToken(userDetails)
        println(token)
        return ResponseEntity.ok(
            mapOf(
                ("token" to token),
                ("auth" to userDetails)
            )
        )
    }

    @PostMapping("/linkGoogle")
    @OWNER
    fun linkGoogle(@RequestBody googleAuthRequest: GoogleAuthRequest): ResponseEntity<UserModel> {
        return ResponseEntity.ok(
            authenticationService.linkWithGoogle(googleAuthRequest.authKey, googleAuthRequest.googleId)
        )
    }


    @PostMapping("/unlinkCredential")
    @OWNER
    fun unlinkGoogle(@RequestBody unlinkRequest: UnlinkCredentialRequest): ResponseEntity<AppUserDetails> {
        return ResponseEntity.ok(
            authenticationService.unlinkCredential(unlinkRequest.credentialId)
        )
    }

    @PostMapping("/register")
    fun register(@RequestBody creationRequest: AccountCreationRequest): ResponseEntity<Account> {
        return ResponseEntity.ok(authenticationService.createAccount(creationRequest, AccountType.DEFAULT))
    }

    @PostMapping("/switch")
    @NOTDEVELOPER
    fun switchAccount(@RequestBody switchAccountRequest: SwitchAccountRequest): ResponseEntity<Map<String, Any>> {
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
    fun getLoggedInUserDetails(): ResponseEntity<AppUserDetails> {
        println("getting user data")
        return ResponseEntity.ok(authenticationService.getDataFromAccount())
    }

    @PostMapping("/linkUser")
    @OWNER
    fun linkAccount(@RequestBody linkAccountRequest: LinkAccountRequest): ResponseEntity<Account> {
        return ResponseEntity.ok(
            authenticationService.linkUserToAccount(linkAccountRequest)
        )
    }

    @PostMapping("/unlinkUser")
    @OWNER
    fun unlinkUser(@RequestBody unlinkAccountRequest: LinkAccountRequest): ResponseEntity<Boolean> {
        return ResponseEntity.ok(
            authenticationService.unlinkUserFromAccount(unlinkAccountRequest)
        )
    }

}