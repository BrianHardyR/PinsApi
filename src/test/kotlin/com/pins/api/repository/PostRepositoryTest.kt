package com.pins.api.repository

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PostRepositoryTest {

    @Autowired lateinit var postRepository: PostRepository

    @Test
    fun getSentimentByAccountAndPostTest(){

        val sentiment = postRepository.getSentimentByAccountAndPost(2,14)
        assert(sentiment != null)

    }

}