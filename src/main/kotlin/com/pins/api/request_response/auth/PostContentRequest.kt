package com.pins.api.request_response.auth

import com.pins.api.entities.content.Content
import com.pins.api.entities.location.Location
import com.pins.api.request_response.content.PostRequest

data class PostContentRequest(
    val postId : Long?=null,
    val post : PostRequest
)

data class PostRequest(
    val list: List<Content> = emptyList(),
    val location : List<Location> = emptyList(),
)
