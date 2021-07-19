package com.pins.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class HomeController {

    @GetMapping("/")
    fun home() = "Welcome to pins api"

}