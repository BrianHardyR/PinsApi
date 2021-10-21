package com.pins.api.security

import com.pins.api.entities.auth.Account
import com.pins.api.entities.auth.AccountUser
import com.pins.api.exceptions.AccountNotFound
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*


@Component
class JwtTokenUtil : Serializable {

    val ACCESS_TOKEN_VALIDITY_SECONDS: Long = 60 * 60 * 24
    @Value("\${pins.signing.key}")
    lateinit var SIGNING_KEY: String

    fun getUserIdFromToken(token : String?):String{
        return getAllClaimsFromToken(token).get("accessor",String::class.java)
    }

    fun getAccountFromToken(token: String?):String{
        return getAllClaimsFromToken(token).get("accessing",String::class.java)
    }

    fun getUsernameFromToken(token: String?): String {
        return getClaimFromToken(token, Claims::getSubject)
    }

    fun getExpirationDateFromToken(token: String?): Date {
        return getClaimFromToken<Date>(token, Claims::getExpiration)
    }

    fun <T> getClaimFromToken(token: String?, claimsResolver: (Claims) -> T): T {
        val claims: Claims = getAllClaimsFromToken(token)
        return claimsResolver(claims)
    }

    private fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts.parser()
            .setSigningKey(SIGNING_KEY)
            .parseClaimsJws(token)
            .getBody()
    }

    private fun isTokenExpired(token: String?): Boolean {
        val expiration: Date = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(user: AccountUser, account: Account): String {
        return doGenerateToken(user.userName, account.id?.toString() ?: throw AccountNotFound())
    }

    private fun doGenerateToken(userIdentifier: String, accountIdentifier: String): String {
        val claims: Claims = Jwts.claims(
            mapOf(
                "accessor" to userIdentifier,
                "accessing" to accountIdentifier
            )
        )
        claims.put("scopes", Arrays.asList(SimpleGrantedAuthority("ROLE_ADMIN")))
        return Jwts.builder()
            .setClaims(claims)
            .setIssuer("PinsApi@2021")
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
            .signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
            .compact()
    }

    fun validateToken(token: String?, userDetails: AccountUser): Boolean {
        val username = getUserIdFromToken(token)
        return username == userDetails.id?.toString() && !isTokenExpired(token)
    }
}