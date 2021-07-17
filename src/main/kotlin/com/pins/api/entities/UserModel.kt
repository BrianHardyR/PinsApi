package com.pins.api.entities

import com.pins.api.utils.toLong
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import java.time.LocalDateTime

/**
 * Holds basic data about the user
 */

@Node("User")
data class UserModel(
        @Id @GeneratedValue
        // set default as null so that the db can generate an ID
        var ID : Long? = null,
        var createdAt : Long = LocalDateTime.now().toLong(),
        val userName:String,
        val name : String,
        val otherNames: List<String>,
        val profileImage : String = "",
        @Relationship( type = "AUTHENTICATES_WITH")
        val credentials : ArrayList<Credential> = ArrayList()
)