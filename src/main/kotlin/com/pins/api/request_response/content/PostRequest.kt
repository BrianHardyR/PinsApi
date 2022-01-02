package com.pins.api.request_response.content

import com.pins.api.entities.content.Content
import com.pins.api.entities.content.MediaContent
import com.pins.api.entities.content.PostLocation

data class PostRequest(
    val commentOf : Long? = null,
    val contents : List<Content> = emptyList(),
    val media : List<MediaRequest> = emptyList(),
    val post_locations: List<PostLocation> = emptyList()
)

data class MediaRequest(
    val mediaId : Long,
    val media_content: MediaContent
)
