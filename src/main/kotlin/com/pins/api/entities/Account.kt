package com.pins.api.entities

import com.pins.api.utils.toLong
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship
import java.time.LocalDateTime

@Node("Account")
data class Account(
        @Id @GeneratedValue
        var ID : Long? = null,
        var createdAt : Long = LocalDateTime.now().toLong(),
        var type : AccountType = AccountType.DEFAULT,
        var active : Boolean = true,

        /**
         * Other users apart from the owner who have access to the account
         */

        @Relationship(type = "ACCESSIBLE_BY", direction = Relationship.Direction.INCOMING)
        var accountUsers : ArrayList<UserAccountRoles> = ArrayList(),

        /**
         * The account owner
         */

        @Relationship(type = "OWNED_BY", direction = Relationship.Direction.INCOMING)
        var owner : UserModel? = null,
)

enum class AccountType{
    DEFAULT,BUSINESS,ADMIN,DEVELOPER
}