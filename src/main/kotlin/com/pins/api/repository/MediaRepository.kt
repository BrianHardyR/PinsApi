package com.pins.api.repository

import com.pins.api.entities.content.Media
import org.springframework.data.neo4j.repository.Neo4jRepository

interface MediaRepository : Neo4jRepository<Media,Long> {


}