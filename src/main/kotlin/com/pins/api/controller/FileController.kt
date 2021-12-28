package com.pins.api.controller

import com.pins.api.entities.content.MediaType
import com.pins.api.service.FileModel
import com.pins.api.service.FileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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

    @GetMapping("/get/{type}/{name}", produces = [
        org.springframework.http.MediaType.IMAGE_JPEG_VALUE,
        org.springframework.http.MediaType.IMAGE_PNG_VALUE,
        org.springframework.http.MediaType.IMAGE_GIF_VALUE

    ])
    fun get(
        @PathVariable("type") type: MediaType,
        @PathVariable("name") name : String,
        @RequestParam("scale") scale : Int? = null,
        @RequestParam("height") height : Int? = null,
        @RequestParam("width") width : Int? = null
    ):ResponseEntity<*>{
        val resource = if (scale != null) fileService.scale(type,name,scale)
        else fileService.resize(type,name,height,width)

        val file = fileService.get(resource)
        print("Returning data")
        return ResponseEntity.ok()
            .body(file)
    }

}