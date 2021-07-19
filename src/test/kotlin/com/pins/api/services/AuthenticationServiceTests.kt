package com.pins.api.services

import com.pins.api.entities.AccountType
import com.pins.api.entities.Roles
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AuthenticationServiceTests {

    @Autowired
    lateinit var authenticationService: AuthenticationService

    @Test
    fun loginAndAssignTokenTest(){
        val emailPasswordAuthRequest = EmailPasswordAuthRequest(email = "test3@email.com", password = "password")
        val token = authenticationService.loginAndAssignToken(emailPasswordAuthRequest)
        println("Token $token")

    }


    @Test
    fun createAccountTest(){
        val creationRequest = AccountCreationRequest(
            userName = "testUser3",
            name = "First",
            otherNames = listOf("Second","Third"),
            credential = EmailPasswordAuthRequest(
                email = "test3@email.com",
                password = "password"
            )
        )
        val account = authenticationService.createAccount(creationRequest, AccountType.DEFAULT)
        val savedAccount = authenticationService.accountsRepo.findById(account.ID!!)
        assert(savedAccount.isPresent)

    }

    @Test
    fun assignUserToAccountTest(){
        val assignRequest = AssignAccountRequest(
            userId = 7,
            type = AccountType.DEVELOPER
        )
        val account = authenticationService.assignUserToAccount(assignRequest)
        val savedAccount = authenticationService.accountsRepo.findById(account.ID!!)
        assert(savedAccount.isPresent)
    }

    @Test
    fun linkUserToAccountTest(){
        val linkRequest = LinkAccountRequest(
            roleOrdinal = Roles.DEVELOPER.ordinal,
            userIdToLink = 7,
            accountID = 9
        )
        val account = authenticationService.linkUserToAccount(linkRequest)
        assert(account.ID == linkRequest.accountID)
    }

    @Test
    fun temp(){
        val data = authenticationService.accountsRepo.findAccountsByUserId(7)
        println("Temp relationship")
        data.forEach {
            println("$=================\n${it.relationship()}\n")
        }
    }

}