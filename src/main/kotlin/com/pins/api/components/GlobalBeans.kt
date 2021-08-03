package com.pins.api.components

import com.pins.api.entities.AccountType
import com.pins.api.entities.CredentialProvider
import com.pins.api.exceptions.UserNotFound
import com.pins.api.repo.AccountsRepo
import com.pins.api.repo.CredentialsRepo
import com.pins.api.repo.UserRepo
import com.pins.api.security.AppUserDetails
import com.pins.api.security.JwtTokenFilter
import com.pins.api.services.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import javax.security.auth.login.CredentialException

@Component
class GlobalBeans{

    @Autowired
    lateinit var userRepo: UserRepo

    @Autowired
    lateinit var accountsRepo: AccountsRepo

    @Autowired
    lateinit var credentialsRepo: CredentialsRepo

    @Autowired
    lateinit var jwtTokenFilter: JwtTokenFilter

    @Bean
    fun passwordEncoder() : PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun getExecutors() = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    @Autowired
    lateinit var authenticationService: AuthenticationService

    @Bean
    fun getLogginUser() = object : UserDetailsService {
        override fun loadUserByUsername(usernameProvider: String?): UserDetails {

            val parts = usernameProvider?.split(':') ?: throw throw CredentialException("User not found")

            val username = parts[0]
            val provider = CredentialProvider.valueOf(value = parts[1])

            val credentialRef = credentialsRepo.findByIdentifierAndActiveAndProvider(
                username,
                provider = provider
            )
            if (!credentialRef.isPresent) throw CredentialException("User with email $username not found")
            val credential = credentialRef.get()
            val user = userRepo.findUserByCredentialId(credential.ID!!)
            val accountRef = accountsRepo.findByTypeAndOwnerId(AccountType.DEFAULT, user.ID!!)
            if (!accountRef.isPresent) throw UserNotFound("User not found $username")
            val account = accountRef.get()
            val accountsRoles = accountsRepo.findAccountRoleByUserIdAndAccountId(user.ID!!, account.ID!!)
            val linkedAccounts = accountsRepo.findAccountsByUserId(user.ID!!)

            return AppUserDetails(
                user,
                credential,
                account,
                listOf(accountsRoles),
                linkedAccounts,
                user.credentials
            ).also {
                println(it)
            }

        }
    }

}