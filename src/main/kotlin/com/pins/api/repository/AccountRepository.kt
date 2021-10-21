package com.pins.api.repository

import com.pins.api.entities.auth.Account
import org.springframework.data.neo4j.repository.Neo4jRepository

interface AccountRepository : Neo4jRepository<Account, Long> {
}