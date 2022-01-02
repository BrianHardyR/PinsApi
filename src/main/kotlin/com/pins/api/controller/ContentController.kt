package com.pins.api.controller

import com.pins.api.request_response.content.PostRequest
import com.pins.api.request_response.content.SentimentRequest
import com.pins.api.service.ContentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/posts")
    fun getContent(
        @RequestParam("lat") lat:Double? = null,
        @RequestParam("lon") lon:Double? = null,
        @RequestParam("limit") limit : Int = 10,
        @RequestParam("offset") offset : Int = 0
    ) : ResponseEntity<*>{
        return ResponseEntity.ok(contentService.getPostsByLocationProximity(lat, lon, limit, offset))
    }

    @GetMapping("/posts/{id}")
    fun getComments(@PathVariable("id") postId : Long ) : ResponseEntity<*> {
        return ResponseEntity.ok(contentService.getPost(postId))
    }

    @GetMapping("/posts/{id}/comments")
    fun getCommentForPost(
        @PathVariable("id") postId : Long,
        @RequestParam("limit") limit : Int = 10,
        @RequestParam("offset") offset : Int = 0
    ):ResponseEntity<*>{
        return ResponseEntity.ok(contentService.getPostComments(postId, limit, offset))
    }

}