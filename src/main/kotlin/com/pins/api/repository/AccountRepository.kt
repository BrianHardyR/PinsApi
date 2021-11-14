package com.pins.api.repository

import com.pins.api.entities.auth.Account
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import java.util.*

interface AccountRepository : Neo4jRepository<Account, Long> {

    @Query("OPTIONAL MATCH (u:User)-[:OWNER]-(a:Account) WHERE ID(u) = \$ownerAccountUserId RETURN ID(a)")
    fun getAccountIdByOwner(ownerAccountUserId : Long) : Optional<Long>

    @Query("MATCH (a:Account)-[l:LINKED_USERS]-(u:User) WHERE ID(a)=\$accountId AND ID(u)=\$userId RETURN COUNT(l)")
    fun countLinkedUserByAccountAndUser(accountId : Long, userId:Long):Int

    @Query("MATCH (a:Account)-[l:LINKED_USERS]-(u:User) WHERE ID(a)=\$accountId AND ID(u)=\$userId RETURN ID(l)")
    fun getLinkIdByUserAndAccount(accountId : Long, userId:Long):Optional<Long>

    @Query("match (a:Account)-[l:LINKED_USERS]-(u:User) where ID(a) = \$accountId AND ID(u) = \$userId detach delete l")
    fun deleteLinkedUserFromAccount(userId: Long, accountId: Long)

    @Query("match (u:User)-[l:LINKED_USERS]-(a:Account) where ID(u)=\$userId return a")
    fun getLinkedAccountsByUserId(userId: Long):List<Account>

}