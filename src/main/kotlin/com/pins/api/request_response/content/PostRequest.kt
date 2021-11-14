package com.pins.api.request_response.content

import com.pins.api.entities.content.Post

data class PostRequest(
    val postId : Long? = null,
    val post : Post
)
