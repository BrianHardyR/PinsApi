package com.pins.api.service

import com.pins.api.entities.auth.*
import com.pins.api.exceptions.AuthException
import com.pins.api.exceptions.InvalidRequest
import com.pins.api.exceptions.NotSupported
import com.pins.api.exceptions.UserNotFound
import com.pins.api.repository.AccountRepository
import com.pins.api.repository.AccountUserRepository
import com.pins.api.request_response.auth.AuthRequest
import com.pins.api.request_response.auth.RegistrationRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.RuntimeException

@Service
class AuthService : UserDetailsService{


    @Autowired lateinit var accountRepository: AccountRepository
    @Autowired lateinit var accountUserRepository: AccountUserRepository
    @Autowired lateinit var authenticationManager: AuthenticationManager
    @Autowired lateinit var passwordEncoder: PasswordEncoder


    @Transactional(rollbackFor = [RuntimeException::class])
    fun register(request : RegistrationRequest){

        if (!request.valid()) throw InvalidRequest()

        val existingUserRef = accountUserRepository.findAccountUsersByUserName(request.username)
        if (existingUserRef.isPresent) throw InvalidRequest()
        val newAccountUser = AccountUser(
            userName = request.username
        ).apply {
            credentials.add(
               when(request.credential.type){
                   AuthProviderType.EmailAndPassword -> EmailAuthProvider(
                       type = request.credential.type,
                       authIdentifier = request.credential.identifier,
                       secret = passwordEncoder.encode(request.credential.secret)
                   )
                   else -> throw NotSupported()
               }
            )
        }

        val savedUser = accountUserRepository.save(newAccountUser)

        val newAccount = Account(
            owner = savedUser
        )
        val savedAccount = accountRepository.save(newAccount)

    }

    fun login(request: AuthRequest){
        if (!request.valid()) throw AuthException()
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(
            "${request.type.name} ${request.identifier}",request.secret
        ))
    }

    override fun loadUserByUsername(userIdStr: String?): UserDetails {

        userIdStr ?: throw AuthException()

        val type : AuthProviderType = AuthProviderType.valueOf(userIdStr.split(" ",ignoreCase = true)[0])

        val userId = userIdStr.split(" ",ignoreCase = true)[1].toLongOrNull() ?: throw AuthException()

        val user = accountUserRepository.findByIdOrNull(userId) ?: throw UserNotFound()
        val credentials = user.credentials.firstOrNull { it.type == type } ?: throw AuthException()

        return object : UserDetails{
            override fun getAuthorities(): List<out GrantedAuthority> {
                return listOf(LinkType.Owner)
            }

            override fun getPassword(): String {
                return credentials.secret
            }

            override fun getUsername(): String {
                return user.userName
            }

            override fun isAccountNonExpired(): Boolean {
                return user.active
            }

            override fun isAccountNonLocked(): Boolean {
                return user.active
            }

            override fun isCredentialsNonExpired(): Boolean {
                return user.active
            }

            override fun isEnabled(): Boolean {
                return user.active
            }

        }

    }

}