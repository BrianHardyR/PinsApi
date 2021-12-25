package com.pins.api.repository

import com.pins.api.entities.auth.AccountUser
import org.neo4j.driver.types.Relationship
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import java.util.*

interface  AccountUserRepository : Neo4jRepository<AccountUser, Long>{

    fun findAccountUsersByUserName(userName : String): Optional<AccountUser>

    @Query("OPTIONAL MATCH (u:User)-[:CREDENTIALS]-(c:Credential) WHERE ID(c)= \$authProviderId RETURN u")
    fun getAccountUserByCredentials(authProviderId : Long) : Optional<AccountUser>

    @Query("OPTIONAL MATCH (u:User)-[l]-(a:Account) WHERE type(l) = 'LINKED_USERS' AND ID(u) = \$userId AND ID(a) = \$accountId RETURN l")
    fun getRoleByAccountAndUser(accountId: Long, userId:Long): Optional<Relationship>

}