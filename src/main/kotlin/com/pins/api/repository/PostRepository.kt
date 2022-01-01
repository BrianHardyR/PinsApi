package com.pins.api.repository

import com.pins.api.entities.content.Post
import org.neo4j.driver.internal.value.RelationshipValue
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query

interface PostRepository : Neo4jRepository<Post,Long> {

    // match (l:Location)-[*]-(l2:Location)-[:LOCATIONS]-(pl:PostLocations)-[:PINNED_AT]-(p:Post) where l.lat = 23 and l.lon = 25 return p
    // get post related to location
    @Query("MATCH (l:Location)-[*]-(l2:Location)-[:LOCATIONS]-(pl:PostLocations)-[:PINNED_AT]-(p:Post) WHERE l.lat = \$lat AND l.lon = \$lon RETURN p")
    fun getPostRelatedToLocation(lat:Double, lon : Double):List<Post>

    @Query("MATCH (l:Location)-[*]-(l2:Location)-[:LOCATIONS]-(pl:PostLocations)-[:PINNED_AT]-(p:Post) WHERE l.lat = \$lat AND l.lon = \$lon RETURN count(p)")
    fun numberOfPostRelatedToLocation(lat:Double, lon : Double) : Double

    @Query("OPTIONAL MATCH (p:Post)-[s:SENTIMENT]-(a:Account) WHERE ID(a)=2 AND ID(p)=14  RETURN s")
    fun getSentimentByAccountAndPost(accountId : Long, postId: Long): RelationshipValue

}