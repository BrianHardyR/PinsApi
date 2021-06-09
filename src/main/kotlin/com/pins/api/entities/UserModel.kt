package com.pins.api.entities

import com.pins.api.utils.toLong
import org.springframework.data.neo4j.core.schema.*
import java.time.LocalDateTime

@Node("User")
data class UserModel(
        @Id @GeneratedValue
        // set default as null so that the db can generate an ID
        var ID : Long? = null,
        var createdAt : Long = LocalDateTime.now().toLong(),
        @Property(name = "userName")

        val userName:String,
        val name : String,
        val otherNames: List<String>,
        val profileImage : String = "",
        @Relationship( type = "AUTHENTICATES_WITH")
        val credentials : List<Credential> = emptyList()
)