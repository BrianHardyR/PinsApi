package com.pins.api.service

import com.pins.api.entities.auth.AccountUser
import com.pins.api.exceptions.AuthException
import com.pins.api.repository.AccountUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired lateinit var accountUserRepository: AccountUserRepository

    fun getUser(userId : Long) : AccountUser{
        val userRef = accountUserRepository.findById(userId)
        if (!userRef.isPresent) throw AuthException()
        return userRef.get()
    }

}