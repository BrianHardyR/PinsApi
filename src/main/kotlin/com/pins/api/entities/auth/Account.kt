package com.pins.api.entities.auth

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.data.neo4j.core.schema.*
import org.springframework.security.core.GrantedAuthority


@Node("Account")
data class Account(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var owner: AccountUser,
    @Relationship("LINKED_USERS")
    val linkedUsers: MutableSet<LinkedUser> = HashSet()
) {
    fun addLinkedUser(user: LinkedUser) = if (user.user.id != null) linkedUsers.add(user) else false
    fun removeLinkedUser(user: LinkedUser) = linkedUsers.remove(user)
}


enum class LinkType : GrantedAuthority {
    Owner {
        override fun getAuthority(): String = "Owner"
    },
    Developer {
        override fun getAuthority(): String = " Developer"
    },
    Assistant {
        override fun getAuthority(): String = "Assistant"
    },
    Follower{
        override fun getAuthority(): String = "Follower"
    }
}

@RelationshipProperties
data class LinkedUser(
    @Id @GeneratedValue var id : Long,
    val linkType: LinkType,
    @TargetNode
    val user: AccountUser
)
