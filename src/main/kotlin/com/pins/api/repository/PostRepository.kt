package com.pins.api.repository

import com.pins.api.entities.content.Post
import org.springframework.data.neo4j.repository.Neo4jRepository

interface PostRepository : Neo4jRepository<Post,Long> {
}