package com.pins.api.request_response.content

import com.pins.api.entities.content.Sentiment

data class SentimentRequest(
    var postId : Long,
    var sentiment : Sentiment,
)