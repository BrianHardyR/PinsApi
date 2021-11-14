package com.pins.api.controller


import com.pins.api.entities.auth.AuthProviderType
import com.pins.api.entities.auth.LinkType
import com.pins.api.entities.content.ContentType
import com.pins.api.entities.content.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/static")
class StaticController {

    @GetMapping("/linktype")
    fun getLinkTypes() = ResponseEntity.ok(LinkType.values())

    @GetMapping("/authprovider")
    fun getAuthProvider() = ResponseEntity.ok(AuthProviderType.values())

    @GetMapping("/contenttype")
    fun getContentType() = ResponseEntity.ok(ContentType.values())

    @GetMapping("/mediatype")
    fun mediaType() = ResponseEntity.ok(MediaType.values())



}