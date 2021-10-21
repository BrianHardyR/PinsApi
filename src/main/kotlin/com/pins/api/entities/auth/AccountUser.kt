package com.pins.api.entities.auth

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship


@Node("User")
data class AccountUser (
    @Id
    @GeneratedValue
    val id : Long? = null,
    val active : Boolean = true,
    val userName : String,
    @Relationship("CREDENTIALS")
    val credentials : MutableSet<AuthProvider> = mutableSetOf()
)

data class ContactInfo(
    @Id @GeneratedValue
    val id: Long? = null
)