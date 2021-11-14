package com.pins.api.repository

import com.pins.api.entities.auth.AuthProvider
import com.pins.api.entities.auth.AuthProviderType
import org.springframework.data.neo4j.repository.Neo4jRepository
import java.util.*

interface AuthProviderRepository : Neo4jRepository<AuthProvider,Long>{

    fun getAuthProviderByTypeAndAuthIdentifier(type:AuthProviderType,authIdentifier : String) : Optional<AuthProvider>

}