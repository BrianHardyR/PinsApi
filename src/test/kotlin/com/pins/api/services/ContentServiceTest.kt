package com.pins.api.services

import com.google.gson.Gson
import com.pins.api.entities.Coordinates
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithUserDetails

@SpringBootTest
class ContentServiceTest {

    @Autowired
    lateinit var contentService: ContentService

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "getLogginUser",value = "test5@email.com:EMAIL_PASSWORD")
    fun createPostTest() {
        val postRequest = PostRequest(
            content = "Sample Text",
            location = listOf(LocationRequest(
                tagname = "location1",
                coord = Coordinates(lat = 5.1, long = 6.1 )
            )),
            route = listOf(RouteRequest(
                tagname = "route",
                from = Coordinates(lat = 5.1, long = 6.1 ),
                to = Coordinates(lat = 1.1, long = 8.1 )
            )),
            userMentions = listOf(
                UserMentionsRequest(
                tagname = "user",
                userId = 1
            ))
        )
        println(Gson().toJson(postRequest))
        val post = contentService.createPost(postRequest)
        assert(post != null)
    }

}