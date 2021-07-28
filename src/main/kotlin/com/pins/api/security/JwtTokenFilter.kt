package com.pins.api.security

import com.pins.api.repo.AccountsRepo
import com.pins.api.repo.UserRepo
import com.pins.api.services.AuthenticationService
import com.pins.api.utils.JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component

class JwtTokenFilter : OncePerRequestFilter(){

    @Autowired
    lateinit var userRepo: UserRepo

    @Autowired
    lateinit var accountsRepo: AccountsRepo

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    lateinit var authService : AuthenticationService


    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        println("do filter\n")
        println(request)
        println("\n==========================")
        print(response)
        println("\n==========================")
        print(filterChain)

        var token = request.getHeader(HttpHeaders.AUTHORIZATION) ?: ""

        print("\ntoken from header $token\n")

        if (token.isEmpty() || !token.startsWith("Bearer ")){

            filterChain.doFilter(request, response)
            return

        }

        token = token.split(" ")[1].trim()


        val user = authService.getUserByUserName(jwtTokenUtil.getUsernameFromToken(token))

        println("\nuser found\n")

        val account = authService.getAccountById(jwtTokenUtil.getAccountFromToken(token).toLong())

        println("\naccount found\n")

        val accountsRoles = accountsRepo.findAccountRoleByUserIdAndAccountId(user.ID!!,account.ID!!)
        val linkedAccounts = accountsRepo.findAccountsByUserId(user.ID!!)
        val appUserDetails = AppUserDetails(user, user.credentials.first(), account, listOf(accountsRoles), linkedAccounts,user.credentials)

        val authentication = UsernamePasswordAuthenticationToken(
            appUserDetails,
            appUserDetails.credential,
            appUserDetails.authorities
        )

        println("\n${appUserDetails.authorities}\n")

        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)

        println("\nfilter done\n")
    }
}