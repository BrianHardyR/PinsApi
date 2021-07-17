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
        val emailPasswordAuthRequest = EmailPasswordAuthRequest(email = "testuser1@email.com", password = "password")
        authenticationService.loginAndAssignToken(emailPasswordAuthRequest)

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
            userIdToLink = 17,
            accountID = 13
        )
        val account = authenticationService.linkUserToAccount(linkRequest)
        assert(account.ID == linkRequest.accountID)
    }

}