package com.pins.api.repo

import com.pins.api.entities.Post
import org.springframework.data.neo4j.repository.Neo4jRepository

interface PostsRepo : Neo4jRepository<Post, Long> {

}