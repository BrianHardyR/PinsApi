package com.pins.api.controller

import com.pins.api.entities.content.MediaType
import com.pins.api.service.FileModel
import com.pins.api.service.FileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/file")
class FileController {

    @Autowired lateinit var fileService: FileService

    @PostMapping("/upload")
    fun upload(
        @RequestParam("file") file  : MultipartFile,
        @RequestParam("type") type : MediaType,
        @RequestParam("name") name : String? = null
    ):ResponseEntity<*> = ResponseEntity.ok(fileService.save(file, FileModel(name,type)))

    fun get(){

    }

}