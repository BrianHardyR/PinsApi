package com.pins.api.entities.auth


import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node

enum class AuthProviderType {
    EmailAndPassword,
    Google,
    ApiToken,
}

interface AuthProvider {
    val type: AuthProviderType
    val authIdentifier: String
    val secret: String
}

@Node("EmailAuthProvider")
data class EmailAuthProvider(
    @Id @GeneratedValue
    val id: Long? = null,
    override val type: AuthProviderType,
    override val secret: String,
    override val authIdentifier: String
) : AuthProvider