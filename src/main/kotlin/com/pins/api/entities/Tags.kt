package com.pins.api.entities

import com.pins.api.utils.clean
import com.pins.api.utils.now
import com.pins.api.utils.toLong
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

/**
 * Store meta data for content
 */

@Node("Tag")
open class Tag(
    var type: TAG_TYPES? = null,
    var name: String? = null,
    var createdBy : UserModel? = null,
    var createdAt : Long = now().toLong()
) {
    var normalizedName: String?
        get() = name?.clean()
        set(value) {}
}

@Node("Location")
data class Location(
    @Id @GeneratedValue
    var ID : Long? = null,
    var coords : Coordinates,
):Tag()

@Node("Route")
data class Route(
    @Id @GeneratedValue
    var ID : Long? = null,
    var from : Coordinates,
    var to : Coordinates
):Tag()

data class UserMentions(
    @Id @GeneratedValue
    var ID : Long? = null,
    @Relationship("MENTION")
    val userModel: UserModel
):Tag()

@Node("Coordinates")
data class Coordinates(
    val lat : Long,
    val long : Long
)


enum class TAG_TYPES {
    Location, Route, Mentions, Currency
}
