package com.pins.api.entities.content

import com.pins.api.entities.Entity
import com.pins.api.entities.auth.Account
import com.pins.api.entities.location.Location
import org.springframework.data.neo4j.core.schema.*

@Node("Post")
data class Post(
    @Id @GeneratedValue
    var id: Long? = null,
    @Relationship("POST_CONTENT")
    var content : List<Content> = emptyList(),
    @Relationship("PINNED_AT")
    var locations : List<PostLocation> = emptyList(),
    @Relationship("COMMENT_OF")
    var commentOf : Post? = null,
    @Relationship("POST_MEDIA")
    var media: List<Media> = emptyList(),
    @Relationship("BELONGS_TO")
    var account: Account? = null,
    @Relationship("SENTIMENT")
    var sentiment:MutableSet<PostSentiment> = HashSet()
):Entity()

@Node("PostLocations")
data class PostLocation(
    @Id @GeneratedValue
    var id : Long? = null,
    var locations : List<Location> = emptyList(),
    val isRoute : Boolean = locations.size > 1
):Entity()


@RelationshipProperties
data class PostSentiment(
    @Id @GeneratedValue var id : Long? = null,
    var sentiment : Sentiment,
    @TargetNode
    var account : Account
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

enum class Sentiment{
    LIKE,DISLIKE
}