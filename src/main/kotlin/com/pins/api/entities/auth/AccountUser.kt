package com.pins.api.entities.auth

import com.fasterxml.jackson.annotation.JsonIgnore
import com.pins.api.entities.Entity
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


@Node("User")
data class AccountUser (
    @Id
    @GeneratedValue
    val id : Long? = null,
    val active : Boolean = true,
    val userName : String,
    @JsonIgnore
    @Relationship("CREDENTIALS")
    val credentials : MutableSet<AuthProvider> = mutableSetOf()
): Entity()

data class ContactInfo(
    @Id @GeneratedValue
    val id: Long? = null
):Entity()

data class PinUserDetails(
    val accountUser: AccountUser,
    val authProvider: AuthProvider,
    val account: Account,
    val linkedAccounts : List<Account> = emptyList(),
    val roles : List<LinkType>
) : UserDetails {
    override fun getAuthorities(): List<out GrantedAuthority> {
        return roles
    }

    override fun getPassword(): String {
        return authProvider.secret
    }

    override fun getUsername(): String {
        return accountUser.userName
    }

    override fun isAccountNonExpired(): Boolean {
        return accountUser.active
    }

    override fun isAccountNonLocked(): Boolean {
        return accountUser.active
    }

    override fun isCredentialsNonExpired(): Boolean {
        return accountUser.active
    }

    override fun isEnabled(): Boolean {
        return accountUser.active
    }
}