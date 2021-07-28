package com.pins.api.entities

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.GrantedAuthority

@RelationshipProperties()
data class UserAccountRoles(
        @Id @GeneratedValue
        var ID: Long? = null,
        var role: Roles,
        @TargetNode
        var userModel: UserModel
)

/**
 * Enum class that holds supported system roles
 * Manager => Can post pins change profile
 * Writer => Can post and reply on pins
 * Assistant => Can only reply to pins
 * Developer => Has manager permissions but only over API
 */

enum class Roles(val friendlyName: String, description: String) : GrantedAuthority {
        OWNER("Owner", "Account Owner") {
                override fun getAuthority() = name
        },
        MANAGER("Manager" , "Can post and reply pins change profile"){
                override fun getAuthority() = name
        },
        WRITER("Writer" , "Can post and reply on pins"){
                override fun getAuthority() = name
        },
        ASSISTANT("Assistant" , "Can only reply to pins"){
                override fun getAuthority() = name
        },
        DEVELOPER("Developer" , "Has manager permissions but only over API"){
                override fun getAuthority() = name
        }
}


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('OWNER')")
annotation class OWNER

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('MANAGER')")
annotation class MANAGER

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('WRITER')")
annotation class WRITER


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ASSISTANT')")
annotation class ASSISTANT

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('DEVELOPER')")
annotation class DEVELOPER

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("!hasAuthority('OWNER')")
annotation class NOTOWNER

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("!hasAuthority('MANAGER')")
annotation class NOTMANAGER

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("!hasAuthority('WRITER')")
annotation class NOTWRITER


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("!hasAuthority('ASSISTANT')")
annotation class NOTASSISTANT

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)

@PreAuthorize("!hasAuthority('DEVELOPER')")
annotation class NOTDEVELOPER
