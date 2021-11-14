package com.pins.api.controller

import com.pins.api.repository.AccountRepository
import com.pins.api.request_response.account.LinkRequest
import com.pins.api.request_response.account.SwitchAccountRequest
import com.pins.api.service.AccountService
import com.pins.api.utils.getAccount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/account")
class AccountController {


    @Autowired lateinit var accountService : AccountService
    @Autowired lateinit var accountRepository: AccountRepository

    @PostMapping("/link")
    fun linkUserToUser(@RequestBody request : LinkRequest) : ResponseEntity<*> {
        return ResponseEntity.ok(accountService.linkUser(request))
    }

    @PostMapping("/unlink")
    fun unlinkUserToUser(@RequestBody request : LinkRequest) : ResponseEntity<*> {
        return ResponseEntity.ok(accountService.unlinkUser(request))
    }

    @GetMapping("/get")
    fun loggedInUser() = ResponseEntity.ok(getAccount())

    @GetMapping("/get/{id}")
    fun get(@PathVariable("id") id : Long) = ResponseEntity.ok(accountRepository.findById(id))

    @GetMapping("/get/{id}/owner")
    fun getAccountForOwner(@PathVariable("id") id : Long) = ResponseEntity.ok(accountService.getAccountByOwner(id))

    @PostMapping("/switch")
    fun switchAccount(@RequestBody request : SwitchAccountRequest) = ResponseEntity.ok(accountService.switchAccount(request.accountId))

}