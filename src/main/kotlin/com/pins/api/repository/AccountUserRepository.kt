package com.pins.api.repository

import com.pins.api.entities.auth.AccountUser
import org.springframework.data.neo4j.repository.Neo4jRepository
import java.util.*

interface  AccountUserRepository : Neo4jRepository<AccountUser, Long>{

    fun findAccountUsersByUserName(userName : String): Optional<AccountUser>

}