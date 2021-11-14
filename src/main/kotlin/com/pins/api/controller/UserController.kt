package com.pins.api.controller

import com.pins.api.repository.AccountUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/user")
class UserController {

    @Autowired lateinit var accountUserRepository: AccountUserRepository


    @GetMapping("/{id}")
    fun get(@PathVariable("id") id : Long ) = ResponseEntity.ok(accountUserRepository.findById(id))

//    @GetMapping("/swwitchAccount")
//    fun switchAccount(@RequestBody )

}