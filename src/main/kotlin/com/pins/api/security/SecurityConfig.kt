package com.pins.api.security

import com.fasterxml.jackson.annotation.JsonIgnore
import com.pins.api.entities.Account
import com.pins.api.entities.AccountType
import com.pins.api.entities.Credential
import com.pins.api.entities.UserModel
import com.pins.api.exceptions.UserNotFound
import com.pins.api.repo.AccountAndUserRoles
import com.pins.api.repo.AccountsRepo
import com.pins.api.repo.CredentialsRepo
import com.pins.api.repo.UserRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import javax.security.auth.login.CredentialException
import javax.servlet.http.HttpServletResponse


@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var userRepo: UserRepo

    @Autowired
    lateinit var accountsRepo: AccountsRepo

    @Autowired
    lateinit var credentialsRepo: CredentialsRepo

    @Autowired
    lateinit var jwtTokenFilter: JwtTokenFilter

    override fun configure(auth: AuthenticationManagerBuilder?) {


        auth?.userDetailsService(
            object : UserDetailsService {
                override fun loadUserByUsername(username: String?): UserDetails {

                    val credentialRef = credentialsRepo.findByIdentifier(
                        username ?: throw CredentialException("User with email $username not found")
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
                        linkedAccounts
                    ).also {
                        println(it)
                    }

                }
            }
        )


    }


    override fun configure(http: HttpSecurity?) {
        http?.cors()
            ?.and()
            ?.csrf()?.disable()
            ?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ?.and()?.exceptionHandling()?.authenticationEntryPoint { request, response, authException ->
                response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    authException.message
                )
            }?.and()
            ?.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            ?.authorizeRequests()
            ?.antMatchers("/")?.permitAll()
            ?.antMatchers("/auth/login")?.permitAll()
            ?.antMatchers("/auth/register")?.permitAll()
            ?.anyRequest()?.authenticated()
    }


    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}

class AppUserDetails(
    val user: UserModel,
    val credential: Credential,
    val account: Account,
    @JsonIgnore
    private val accounts: Collection<AccountAndUserRoles>,
    val linkedAccounts: Collection<Account>
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        ArrayList(accounts.map { it.relationship() })

    override fun getPassword() = credential.secret.also {
        println("Password in db : $it")
    }

    override fun getUsername() = user.userName

    override fun isAccountNonExpired() = account.active

    override fun isAccountNonLocked() = account.active

    override fun isCredentialsNonExpired() = credential.active

    override fun isEnabled(): Boolean = true

}