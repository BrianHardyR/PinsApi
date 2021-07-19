package com.pins.api.utils

import com.pins.api.security.AppUserDetails
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*


@Component
class JwtTokenUtil : Serializable {
    companion object{
        val SIGNING_KEY = "9XvAQAW4dDriwfeY7yKPKRbz4NmCrRTB7EXBEiHzdw9SpeUafMpWnaayMhfTEFvazttZQeePytnNmUDtxRUhrhj5udST7FNrpqwyUqvbyYfT8K2fe8B2d3afagfMi3rYYB3u5NSgp5Hb8czz4VybcGWMtGHMdaCqWhvHxF8hT86CniAgXKVPrNZqpkUgBHUXFCfZvaZ454RqjSS2SXikf7hmJEGpdVyXh2NfXVTL2vFZUUjF24kFR477KYFnPrGJ"
        const val ACCESS_TOKEN_VALIDITY_SECONDS = 60*60
    }
    fun getUsernameFromToken(token: String?): String {

        return getClaimFromToken(token, Claims::getSubject)
    }

    fun getAccountFromToken(token : String?):String{
        return getClaimFromToken(token, Claims::getAudience)
    }

    fun getExpirationDateFromToken(token: String?): Date {
        return getClaimFromToken<Date>(token, Claims::getExpiration)
    }

    fun <T> getClaimFromToken(token: String?, claimsResolver: (Claims) -> T): T {
        val claims: Claims = getAllClaimsFromToken(token)
        return claimsResolver.invoke(claims)
    }

    private fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(SIGNING_KEY)
            .build()
            .parseClaimsJws(token)
            .body
    }

    private fun isTokenExpired(token: String?): Boolean {
        val expiration: Date = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(user: AppUserDetails): String {
        return doGenerateToken(user)
    }

    private fun doGenerateToken(user: AppUserDetails): String {
        val claims = Jwts.claims().setSubject(user.username).setAudience("${user.account.ID}")
        claims.put("scopes", user.authorities)
        return Jwts.builder()
            .setId("${user.account.ID}")
            .setClaims(claims)
            .setIssuer("http://pins.com")
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
            .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
            .compact()
    }

    fun validateToken(token: String?, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
}