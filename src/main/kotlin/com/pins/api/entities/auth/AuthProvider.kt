package com.pins.api.entities.auth


import com.pins.api.entities.Entity
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.security.access.prepost.PreAuthorize

enum class AuthProviderType {
    EmailAndPassword,
    Google,
    ApiToken,
}


@Node("Credential")
data class AuthProvider(
    @Id @GeneratedValue
    val id: Long? = null,
    val type: AuthProviderType,
    val secret: String,
    val authIdentifier: String
): Entity()


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('Owner')")
annotation class OWNER