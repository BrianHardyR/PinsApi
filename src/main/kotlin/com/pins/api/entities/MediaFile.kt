package com.pins.api.entities

import com.pins.api.utils.now
import com.pins.api.utils.toLong
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node("Media")
data class MediaFile(
    @Id @GeneratedValue
    var ID : Long? = null,
    val createdAt: Long = now().toLong(),
    val url: String,
    /**
     * Generate tags from caption
     */
    val caption: String,
    @Relationship(type = "TAGS")
    val tags : ArrayList<ContentTag> = ArrayList()
)