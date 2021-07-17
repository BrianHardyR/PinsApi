package com.pins.api.repo

import com.pins.api.entities.UserModel
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import java.util.*

interface UserRepo : Neo4jRepository<UserModel, Long> {

    @Query("MATCH (u:User)-[:AUTHENTICATES_WITH]-(c:Credential) WHERE ID(c) = \$credentialId return u")
    fun findUserByCredentialId( credentialId: Long ):UserModel

    fun findByUserName(username : String): Optional<UserModel>

}
