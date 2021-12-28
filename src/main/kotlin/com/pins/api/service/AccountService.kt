package com.pins.api.service

import com.pins.api.entities.auth.Account
import com.pins.api.entities.auth.LinkedUser
import com.pins.api.exceptions.AccountNotFound
import com.pins.api.exceptions.InvalidRequest
import com.pins.api.repository.AccountRepository
import com.pins.api.repository.AccountUserRepository
import com.pins.api.request_response.account.LinkRequest
import com.pins.api.security.JwtTokenUtil
import com.pins.api.utils.getPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AccountService {

    @Autowired lateinit var accountRepository: AccountRepository
    @Autowired lateinit var accountUserRepository: AccountUserRepository
    @Autowired lateinit var userService: UserService
    @Autowired lateinit var authService: AuthService
    @Autowired lateinit var jwtTokenUtil: JwtTokenUtil

    fun getAccountByOwner(ownerId : Long):Optional<Account>{
        val accountRef = accountRepository.getAccountIdByOwner(ownerId)
        return if (accountRef.isPresent) accountRepository.findById(accountRef.get())
        else Optional.empty()
    }

    fun getAccount(accountId : Long) : Account{
        val accountRef = accountRepository.findById(accountId)
        if (!accountRef.isPresent) throw AccountNotFound()
        return accountRef.get()
    }

    @Transactional
    fun linkUser(request : LinkRequest) : Account{
        if (!request.valid(true)) return throw InvalidRequest()
        println("linking started")
        val account = com.pins.api.utils.getAccount()
        // user to link to the account
        val user = userService.getUser(request.userId)
        // check that the user is not linked
        val linkCount = accountRepository.countLinkedUserByAccountAndUser(account.id!!, user.id!!)
        val link = LinkedUser(
            linkType = request.linkType!!,
            user = user
        )
        if (linkCount == 1){
            link.id = accountRepository.getLinkIdByUserAndAccount(account.id!!, user.id).get()
        }

        println("linking saving")
        account.addLinkedUser(link)
        return accountRepository.save(account)
    }

    @Transactional
    fun unlinkUser(request: LinkRequest) : Account{
        if (request.valid(false)) return throw InvalidRequest()
        val account = com.pins.api.utils.getAccount()
        accountRepository.deleteLinkedUserFromAccount(request.userId, account.id ?: throw AccountNotFound())
        return getAccount(account.id ?: throw AccountNotFound())
    }

    fun switchAccount(accountId: Long) : Map<String,*> {

        val principal = getPrincipal()
        val accountRef = accountRepository.findById(accountId)
        println("Getting link id")
        println("$accountId => ${principal.accountUser.id}")
        val linkIdRef = accountRepository.getLinkIdByUserAndAccount(accountId,principal.accountUser.id ?: throw AccountNotFound())
        if (!linkIdRef.isPresent || !accountRef.isPresent) throw AccountNotFound()

        return mapOf(
            "token" to jwtTokenUtil.generateToken(principal.accountUser,accountRef.get(),principal.authProvider),
            "user" to principal.accountUser,
            "account" to accountRef.get()
        )

    }


}