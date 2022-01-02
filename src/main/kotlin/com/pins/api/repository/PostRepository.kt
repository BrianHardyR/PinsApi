package com.pins.api.repository

import com.pins.api.entities.content.Post
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query

interface PostRepository : Neo4jRepository<Post, Long> {

    // match (l:Location)-[*]-(l2:Location)-[:LOCATIONS]-(pl:PostLocations)-[:PINNED_AT]-(p:Post) where l.lat = 23 and l.lon = 25 return p
    // get post related to location
    @Query("MATCH (l:Location)-[*]-(l2:Location)-[:LOCATIONS]-(pl:PostLocations)-[:PINNED_AT]-(p:Post) WHERE l.lat = \$lat AND l.lon = \$lon RETURN p")
    fun getPostRelatedToLocation(lat: Double, lon: Double): List<Post>

    @Query("MATCH (l:Location)-[*]-(l2:Location)-[:LOCATIONS]-(pl:PostLocations)-[:PINNED_AT]-(p:Post) WHERE l.lat = \$lat AND l.lon = \$lon RETURN count(p)")
    fun numberOfPostRelatedToLocation(lat: Double, lon: Double): Double

    @Query("MATCH (p:Post)-[s:SENTIMENT]-(a:Account) WHERE ID(p)=\$postId AND ID(a)=\$accountId DETACH DELETE s")
    fun deleteSentiment(accountId: Long, postId: Long)

    // match (l:Location)-[*..10]-(p:Post) where not (p)-[:COMMENT_OF]->() return distinct p order by  p.createdAt desc  skip 0 limit 1

    @Query("MATCH (l:Location{ lat:\$lat,lon:\$lon })-[*..10]-(p:Post) WHERE NOT (p)-[:COMMENT_OF]->() RETURN DISTINCT p ORDER BY  p.createdAt DESC SKIP \$offset LIMIT \$limit")
    fun getPostByLocation(lat:Double, lon: Double, limit : Int = 10, offset : Int = 0):List<Post>

    @Query("MATCH (l:Location)-[*..10]-(p:Post) WHERE NOT (p)-[:COMMENT_OF]->() RETURN DISTINCT p ORDER BY  p.createdAt DESC SKIP \$offset LIMIT \$limit")
    fun getPosts(limit: Int = 0, offset: Int = 0):List<Post>

    @Query("MATCH (p:Post)<-[:COMMENT_OF]-(c:Post) WHERE ID(p)=2 RETURN DISTINCT c ORDER BY c.createdAt DESC SKIP \$offset LIMIT \$limit")
    fun getCommentByPost(postId : Long, limit: Int = 0, offset: Int = 0):List<Post>

}
