package com.pins.api.entities

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode
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
