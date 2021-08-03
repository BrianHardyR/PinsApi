package com.pins.api.controller

import com.pins.api.entities.Post
import com.pins.api.services.ContentService
import com.pins.api.services.PostRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/content")
class ContentController {

    @Autowired
    lateinit var contentService: ContentService

    @PostMapping("/create")
    fun create(@RequestBody  postRequest: PostRequest ) : ResponseEntity<Post>{
        return ResponseEntity.ok(contentService.createPost(postRequest))
    }

}