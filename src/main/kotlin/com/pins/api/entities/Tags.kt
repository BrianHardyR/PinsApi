package com.pins.api.entities

import com.pins.api.utils.clean
import com.pins.api.utils.now
import com.pins.api.utils.toLong
import org.springframework.data.neo4j.core.schema.*

/**
 * Store meta data for content
 */

@Node("Tag")
class Tags():ContentTag()

open class ContentTag(
    @Id @GeneratedValue
    var ID : Long? = null,
    var type: TAG_TYPES? = null,
    var name: String? = null,
    @Relationship(value = "CREATED_BY")
    var createdBy : UserModel? = null,
    var createdAt : Long = now().toLong()
) {
    val normalizedName: String
        get() = name?.clean() ?: ""

}


@RelationshipProperties
data class Location(
    @TargetNode
    var coords : Coordinates,
):ContentTag()

@Node("Route")
data class Route(
    @Relationship(value = "ROUTE_FROM")
    var from : Coordinates,
    @Relationship(value = "ROUTE_TO")
    var to : Coordinates,
    val routeKey : Long = "${from.ID}${to.ID}".toLong()
):ContentTag()

@RelationshipProperties
data class UserMentions(
    @TargetNode
    var userModel: UserModel
):ContentTag()

@Node("Coordinates")
data class Coordinates(
    val lat : Double,
    val long : Double,
    @Id
    var ID : Long? = ("$lat".replace(".","")+"$long".replace(".","")).toLong(),
)


enum class TAG_TYPES {
    Location, Route, Mentions, Currency, Hashtags
}
