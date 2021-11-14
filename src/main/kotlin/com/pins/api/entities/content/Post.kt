package com.pins.api.entities.content

import com.pins.api.entities.Entity
import com.pins.api.entities.auth.Account
import com.pins.api.entities.location.Location
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node("Post")
data class Post(
    @Id @GeneratedValue
    var id: Long? = null,
    @Relationship("POST_CONTENT")
    val content : List<Content> = emptyList(),
    @Relationship("PINNED_AT")
    val locations : List<PostLocation> = emptyList(),
    @Relationship("COMMENT_OF")
    val commentOf : Post? = null,
    @Relationship("POST_MEDIA")
    val media: List<Media> = emptyList(),
    @Relationship("BELONGS_TO")
    val account: Account
):Entity()

@Node("PostLocations")
data class PostLocation(
    @Id @GeneratedValue
    var id : Long? = null,
    var locations : List<Location> = emptyList(),
    val isRoute : Boolean = locations.size == 3
):Entity()

@Node("Content")
data class Content(
    @Id @GeneratedValue
    var id: Long? = null,
    var type : ContentType,
    /**
     * Each text represent paragraphs for ContentType#String
     * Each text represents a list item for ContentType#List
     */
    var text : List<String> = emptyList()
):Entity()

enum class ContentType{
    String,List
}