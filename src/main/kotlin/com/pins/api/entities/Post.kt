package com.pins.api.entities

import com.pins.api.utils.now
import com.pins.api.utils.toLong
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

/**
 * The user may post
 * 1. Messages
 * 2. Polls
 * 3. Question extension of polls
 * 4. Comments
 *  **** All have Tags
 *  **** All may !! contain media files video , images or both
 *  Tags may contain
 *  1. Location
 *  2. Route
 *  3. User mentions
 *  4. Currency/ money
 *  5. Mentions/ Hashtags
 */

@Node("Post")
data class Post(
    @Id @GeneratedValue
    var ID: Long? = null,
    val createdAt: Long = now().toLong(),
    var content: String,
    @Relationship(type = "BELONGS_TO")
    var belongsTo: Account,
    @Relationship(type = "POSTED_BY")
    var postedBy: UserModel,
    @Relationship(type = "COMMENTS")
    val comments : ArrayList<Post> = ArrayList(),
    val locations : ArrayList<Location> = ArrayList(),
    val routes : ArrayList<Route> = ArrayList(),
    val userMentions : ArrayList<UserMentions> = ArrayList(),
    ) {
    var updateAt: Long
        get() = now().toLong()
        set(value) {}
}

//@RelationshipProperties
//data class Comments(
//    @Id @GeneratedValue
//    var ID: Long? = null,
//    val commentedOn: Post,
//    @TargetNode
//    val comment : Post
//)