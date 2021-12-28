package com.pins.api.service

import com.pins.api.entities.content.MediaType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FileServiceTest {

    @Autowired lateinit var fileService: FileService

    @Test
    fun cropTest(){
        val resourcePath = fileService.resize(MediaType.Image,"Image_1640517691180.png",100,100)
        val resource = fileService.get(resourcePath)
        assert(resource.exists())
    }

    @Test
    fun cropTest2(){
        val resourcePath = fileService.scale(MediaType.Image,"Image_1640517691180.png",5000)
        val resource = fileService.get(resourcePath)
        assert(resource.exists())
    }

}