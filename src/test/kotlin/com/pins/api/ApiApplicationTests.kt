package com.pins.api

import com.pins.api.controller.AccountController
import com.pins.api.entities.auth.Account
import com.pins.api.entities.auth.AccountUser
import com.pins.api.entities.location.Location
import com.pins.api.repository.AccountRepository
import com.pins.api.repository.AccountUserRepository
import com.pins.api.repository.PostRepository
import com.pins.api.service.LocationService
import com.pins.api.utils.clean
import com.pins.api.utils.getPrincipal
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithUserDetails

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    lateinit var accountRepository: AccountRepository
    @Autowired
    lateinit var accountUserRepository: AccountUserRepository
    @Autowired
    lateinit var locationService: LocationService
    @Autowired
    lateinit var postRepository: PostRepository
    @Autowired
    lateinit var accountController: AccountController


    @Test
    fun passwordEncoderTest() {

        val password = "qwertyuiop"
        val encoded = passwordEncoder.encode(password)
        val otherEncoded = passwordEncoder.encode(password)

        assert(passwordEncoder.matches(password, encoded))
        assert(passwordEncoder.matches(password, otherEncoded))

    }


    @Test
    fun cleanTest() {
        val text = "Brian Elias ?"
        val cleaned = text.clean()
        println(cleaned)
        assert(cleaned == "brianelias")
    }

    @Test
    fun accountMapping() {
        var user = AccountUser(userName = "Test")
        val savedUser = accountUserRepository.save(user)
        var account = Account(owner = savedUser)
        val savedAccount = accountRepository.save(account)
        assert(savedAccount is Account && savedAccount.id != null)
    }

    @Test
    fun saveLocationTest() {
        locationService.saveLocation(
            Location(
                lat = 123.11117777888,
                lon = 23.891177789967
            )
        )
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "getAuthenticationService",value = "EmailAndPassword test@email.com")
    fun authTest(){
        val user = getPrincipal()
        println("Authenticated user")
        println(user.roles)
    }

    @Test
    fun getLinkedAccounts(){
        val linkedAccounts = accountRepository.getLinkedAccountsByUserId(9)
        println("Linked accounts")
        println(linkedAccounts.map { it.id })
    }

}
