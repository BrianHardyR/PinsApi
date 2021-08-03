package com.pins.api.repo

import com.pins.api.entities.*
import org.springframework.data.neo4j.repository.Neo4jRepository
import java.util.*

interface TagRepo : Neo4jRepository<Tags, Long>
interface ContentTagRepo : Neo4jRepository<ContentTag, Long>
interface LocationTagRepo : Neo4jRepository<Location, Long>
interface RouteTagRepo : Neo4jRepository<Route, Long>{
    fun findByRouteKey(routeKey: Long): Optional<Route>
}
interface UserMentionTagRepo : Neo4jRepository<UserMentions, Long>
interface CoordinatesTagRepo : Neo4jRepository<Coordinates, Long>