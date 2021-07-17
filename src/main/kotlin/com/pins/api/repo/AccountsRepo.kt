package com.pins.api.repo

import com.pins.api.entities.Account
import com.pins.api.entities.AccountType
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import java.util.*

interface AccountsRepo : Neo4jRepository<Account, Long> {

    /**
     * Returns accounts the user has access to other than their own
     */

    @Query("MATCH (u:User)-[:ACCESSIBLE_BY]-(a:Account) WHERE ID(u) = \$userId RETURN a")
    fun findAccountsByUserId( userId : Long ):Collection<Account>

    /**
     * A single may have multiple accounts
     * @see [AccountType]
     */

    @Query("MATCH (u:User)-[:OWNED_BY]-(a:Account) WHERE ID(u) = \$userId RETURN a")
    fun findAccountByOwner( userId : Long ): Collection<Account>

    @Query("MATCH (o:User)-[:OWNED_BY]-(a:Account) WHERE ID(o)=\$ownerId AND a.type = \$type RETURN a")
    fun findByTypeAndOwnerId( type : AccountType, ownerId : Long ) : Optional<Account>




}