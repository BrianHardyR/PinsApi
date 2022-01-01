package com.pins.api.controller

import com.pins.api.request_response.content.PostRequest
import com.pins.api.request_response.content.SentimentRequest
import com.pins.api.service.ContentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/content")
class ContentController {

    @Autowired lateinit var contentService: ContentService

    @PostMapping("/post")
    fun postContent(@RequestBody request : PostRequest): ResponseEntity<*>{
        return ResponseEntity.ok(contentService.savePost(request))
    }

    @PostMapping("/sentiment")
    fun postSentiment(@RequestBody request : SentimentRequest):ResponseEntity<*>{
        return ResponseEntity.ok(contentService.saveSentiment(request))
    }

}