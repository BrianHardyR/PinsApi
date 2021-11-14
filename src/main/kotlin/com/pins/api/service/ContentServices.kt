package com.pins.api.service

import com.pins.api.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ContentServices {

    @Autowired lateinit var postRepository: PostRepository

}