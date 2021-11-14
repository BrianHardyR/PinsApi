package com.pins.api.service

import com.pins.api.entities.auth.*
import com.pins.api.exceptions.AccountNotFound
import com.pins.api.exceptions.AuthException
import com.pins.api.exceptions.InvalidRequest
import com.pins.api.exceptions.UserNotFound
import com.pins.api.repository.AccountRepository
import com.pins.api.repository.AccountUserRepository
import com.pins.api.repository.AuthProviderRepository
import com.pins.api.request_response.auth.AuthRequest
import com.pins.api.request_response.auth.RegistrationRequest
import com.pins.api.security.JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService : UserDetailsService {


    @Autowired
    lateinit var accountRepository: AccountRepository
    @Autowired
    lateinit var accountUserRepository: AccountUserRepository
    @Autowired
    lateinit var authenticationManager: AuthenticationManager
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    lateinit var authProviderRepository: AuthProviderRepository
    @Autowired
    lateinit var accountService: AccountService
    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil


    @Transactional
    fun register(request: RegistrationRequest) : Map<String,*>{

        if (!request.valid()) throw InvalidRequest()

        val existingUserRef = accountUserRepository.findAccountUsersByUserName(request.username)
        if (existingUserRef.isPresent) throw InvalidRequest()

        val existingAuthProviderRef = authProviderRepository.getAuthProviderByTypeAndAuthIdentifier(
            request.credential.type,
            request.credential.identifier
        )
        if (existingAuthProviderRef.isPresent) throw InvalidRequest()

        val newAccountUser = AccountUser(
            userName = request.username
        ).apply {
            credentials.add(
                AuthProvider(
                    type = request.credential.type,
                    authIdentifier = request.credential.identifier,
                    secret = passwordEncoder.encode(request.credential.secret)
                )
            )
        }

        val savedUser = accountUserRepository.save(newAccountUser)

        val newAccount = Account(
            owner = savedUser
        )
        val savedAccount = accountRepository.save(newAccount)
        println("accountRepository saved  now starting login process")
        return login(request.credential)
    }

    fun login(request: AuthRequest) : Map<String,*>{
        if (!request.valid()) throw AuthException()
        val authenticate = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                "${request.type.name} ${request.identifier}", request.secret
            )
        )

        val principal = authenticate.principal as PinUserDetails
        println("Getting authentication principal ${principal.toString()}")
        // account for owner
        return mapOf(
            "token" to jwtTokenUtil.generateToken(principal.accountUser,principal.account),
            "user" to principal.accountUser,
            "account" to principal.account
        )

    }

    override fun loadUserByUsername(userIdStr: String?): UserDetails {



        userIdStr ?: throw AuthException()

        val type: AuthProviderType = AuthProviderType.valueOf(userIdStr.split(" ", ignoreCase = true)[0])
        val authIdentifier = userIdStr.split(" ", ignoreCase = true)[1]

        val authProviderRef = authProviderRepository.getAuthProviderByTypeAndAuthIdentifier(type,authIdentifier)
        println("auth provider fetched")
        if (!authProviderRef.isPresent) throw AuthException()
        val authProvider = authProviderRef.get()
        // get user using the credentials
        val accountUserRef = accountUserRepository.getAccountUserByCredentials(authProviderRef.get().id ?: throw AuthException())
        if (!accountUserRef.isPresent) throw UserNotFound()
        println("user fetched")
        val accountUser = accountUserRef.get()
        val accountRef = accountService.getAccountByOwner(accountUser.id ?: throw UserNotFound())
        println("account fetched")
        // get accounts that belong to the user
        if (!accountRef.isPresent) throw AccountNotFound()
        val account = accountRef.get()
        return PinUserDetails(accountUser,authProvider,account)

    }

}