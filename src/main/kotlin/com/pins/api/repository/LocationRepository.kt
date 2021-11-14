package com.pins.api.repository

import com.pins.api.entities.location.Location
import org.springframework.data.neo4j.repository.Neo4jRepository

interface LocationRepository : Neo4jRepository<Location,Long> {




}