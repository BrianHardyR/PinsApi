package com.pins.api.utils

import com.pins.api.entities.CredentialProvider
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
        val SIGNING_KEY = "uEsdmnm5rGcM8ebvOcZBr3PHFJhvKiWn2Us7AFPMoIey53so17rvAobdpUz29AvLLbvDy627EFiFhGI8WXWIzx5g7c1tDsoAKFXJ2onho4neF1FagIWlENmA2AZtt8M8kw56cpneQ61mxt6dRFdxtpnetgVqzQLZvRKja6zBpaHbkNLfB6fiCMfrKr5gVF8lCFBhkXGBZaMhM949h7qrmxGOQLnZw1IqJnwFlDyR2u9IsUc7Qz59WlhPpch3cVifpdNEd3o4lw94cBZu6PHxVXMCnD7Jo1vZU4ZdiqETYEAGcudQeKOOWNXm5M5bhBsDUKE202GWGCzdshx3e72N7j44nd9cFiqvyB7kv4HDGhu587oWxC0yp08JCfn3Kctp4gQ85GH5kvallFmkVQrDBocsXk9O6OvK8S5oVaxSscYE19SIDETdnynGqf1XFPuJ8axj03W6h8xGvZwkH4SLjRQQba4R3BG2qYvNN2JXwXGvv0xBOdcDYpp06XakgHlC"
        const val ACCESS_TOKEN_VALIDITY_SECONDS = 60*60*24
    }
    fun getUsernameFromToken(token: String?): String {

        return getClaimFromToken(token, Claims::getSubject)
    }

    fun getAccountFromToken(token : String?):String{
        return getClaimFromToken(token, Claims::getAudience)
    }

    fun getIssuer(token: String?):Issuer{
        return getClaimFromToken(token, Claims::getIssuer).let { Issuer.valueOf(it) }
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
            .setIssuer(
                when(user.credential.provider){
                    CredentialProvider.EMAIL_PASSWORD -> Issuer.PINS.name
                    CredentialProvider.GOOGLE -> Issuer.GOOGLE.name
                    else -> Issuer.PINS.name
                }
            )
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
            .signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
            .compact()
    }

    fun validateToken(token: String?, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
}

enum class Issuer(val link : String){
    PINS("https://pins.com"),
    GOOGLE("https://google.com")
}