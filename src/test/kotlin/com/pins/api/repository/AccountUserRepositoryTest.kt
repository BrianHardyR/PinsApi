package com.pins.api.repository

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AccountUserRepositoryTest {

    @Autowired lateinit var accountUserRepository: AccountUserRepository

    @Test
    fun getRoleByAccountAndUserTest(){
        val userId = 48.toLong()
        val accountId = 47.toLong()

        val role = accountUserRepository.getRoleByAccountAndUser(accountId, userId)
        assert(role.isPresent)
    }

}