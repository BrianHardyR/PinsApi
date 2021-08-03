package com.pins.api.repo

import com.pins.api.entities.Account
import com.pins.api.entities.AccountType
import com.pins.api.entities.Roles
import org.neo4j.driver.internal.value.RelationshipValue
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import java.util.*

interface AccountsRepo : Neo4jRepository<Account, Long> {

    /**
     * Returns accounts the user has access to including their own
     */

    @Query("MATCH (u:User)-[r:ACCESSIBLE_BY]-(a:Account) WHERE ID(u) = \$userId RETURN a")
    fun findAccountsByUserId( userId : Long ):Collection<Account>

    @Query("MATCH (u:User)-[r:ACCESSIBLE_BY]-(a:Account) WHERE ID(u) = \$userId and ID(a)= \$accountId RETURN a as account,r as accountRole")
    fun findAccountRoleByUserIdAndAccountId( userId : Long , accountId : Long):AccountAndUserRoles


    /**
     * A single may have multiple accounts
     * @see [AccountType]
     */

    @Query("MATCH (u:User)-[:OWNED_BY]-(a:Account) WHERE ID(u) = \$userId RETURN a")
    fun findAccountByOwner( userId : Long ): Collection<Account>

    @Query("MATCH (o:User)-[:OWNED_BY]-(a:Account) WHERE ID(o)=\$ownerId AND a.type = \$type RETURN a")
    fun findByTypeAndOwnerId( type : AccountType, ownerId : Long ) : Optional<Account>



}

data class AccountAndUserRoles(
    val account : Account,
    private val accountRole : RelationshipValue
){
    fun relationship() = Roles.valueOf(accountRole["role"].asString())
}