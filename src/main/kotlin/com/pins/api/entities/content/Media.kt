package com.pins.api.entities.content

import com.pins.api.entities.Entity
import com.pins.api.entities.location.Location
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node("Media")
data class Media(
    @Id @GeneratedValue
    var id : Long? = null,
    val type : MediaType,
    val url : String,
    @Relationship("MEDIA_CONTENT")
    val tag : MediaContent? = null,
): Entity()

@Node("MediaContent")
data class MediaContent(
    @Id @GeneratedValue
    var id : Long? = null,
    var content : String,
    var location : Location? = null
):Entity()

enum class MediaType{
    Image,Gif,Video,Map
}
