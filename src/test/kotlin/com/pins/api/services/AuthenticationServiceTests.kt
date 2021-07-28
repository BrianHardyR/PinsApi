package com.pins.api.services


import com.pins.api.entities.AccountType
import com.pins.api.entities.Roles
import com.pins.api.utils.GoogleAuthUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AuthenticationServiceTests {

    @Autowired
    lateinit var authenticationService: AuthenticationService

    val authCode = "4/0AX4XfWisCASHhn8VoP8x6iRrztUp9AGYokJhO5UnDamhqO4KEvLw-_FhgoNf6RNLSL7LXw"

    val refreshToken = "1//030TD2T5TNMvECgYIARAAGAMSNwF-L9IromQz3F-82xlGC1qAglzhvnSwTF-vTNa4HN6eU3YD7jv2U6pHuTNGzWEskNlHE-nh_Mw"

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
            roleOrdinal = Roles.ASSISTANT.ordinal,
            userIdToLink = 2,
            accountID = 15
        )
        val account = authenticationService.linkUserToAccount(linkRequest)
        assert(account.ID == linkRequest.accountID)
    }

    @Test
    fun temp(){
        val data = authenticationService.accountsRepo.findAccountsByUserId(7)
        println("Temp relationship")
        data.forEach {
            println("$=================\n${it}\n")
        }
    }

    @Test
    fun signInWithGoogleTest(){
        authenticationService.signInWithGoogle(authCode)
    }

    @Test
    fun refreshTokenTest(){
        GoogleAuthUtil.refreshToken(refreshToken)
    }

}