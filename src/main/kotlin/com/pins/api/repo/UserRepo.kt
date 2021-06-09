package com.pins.api.repo

import com.pins.api.entities.UserModel
import org.springframework.data.neo4j.repository.Neo4jRepository

interface UserRepo : Neo4jRepository<UserModel, Long> {

}