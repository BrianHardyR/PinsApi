package com.pins.api.security

import com.pins.api.entities.auth.LinkType
import com.pins.api.entities.auth.PinUserDetails
import com.pins.api.exceptions.AuthException
import com.pins.api.exceptions.UserNotFound
import com.pins.api.repository.AccountRepository
import com.pins.api.repository.AccountUserRepository
import com.pins.api.repository.AuthProviderRepository
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
class JwtTokenFilter : OncePerRequestFilter() {

    @Autowired
    lateinit var accountRepository: AccountRepository
    @Autowired
    lateinit var accountUserRepository: AccountUserRepository
    @Autowired
    lateinit var authProviderRepository: AuthProviderRepository

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        println("Jwt Filtering")
        println(request.method+":"+request.servletPath)

        val header = request.getHeader(HttpHeaders.AUTHORIZATION)

        println(header)

        if (header.isNullOrEmpty() || !header.startsWith("Bearer")){
            println("No token from request")
            filterChain.doFilter(request, response)
            return
        }

        val token = header.split(" ")[1].trim()

        println("Token : $token")

        val userId = jwtTokenUtil.getUserIdFromToken(token).toLong()
        val userRef = accountUserRepository.findById(userId)

        if (!userRef.isPresent) throw AuthException()

        val valid = jwtTokenUtil.validateToken(token,userRef.get())
        if (!valid) throw AuthException()

        val accountId = jwtTokenUtil.getAccountFromToken(token).toLong()
        val accountRef = accountRepository.findById(accountId)
        if (!accountRef.isPresent) throw AuthException()

        val account = accountRef.get()
        val roles = mutableListOf<LinkType>()

        val authProviderId = jwtTokenUtil.getAuthProviderFromToken(token).toLong()
        val authProviderRef = authProviderRepository.findById(authProviderId)



        if (account.owner?.id == userId) roles.add(LinkType.Owner)

        accountUserRepository.getRoleByAccountAndUser(accountId, userId).run {
            if (isPresent) roles.add(get())
        }


        if (roles.isEmpty()) throw AuthException()

        println("ROLES")
        println(roles.map { it.authority })

        val authentication = UsernamePasswordAuthenticationToken(
            PinUserDetails(userRef.get(),authProviderRef.get(),account,accountRepository.getLinkedAccountsByUserId(userRef.get().id ?: throw UserNotFound()),roles.toList()),
            null,
            roles
        )
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)

    }
}