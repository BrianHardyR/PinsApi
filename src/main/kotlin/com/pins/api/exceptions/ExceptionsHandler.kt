package com.pins.api.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import javax.security.auth.login.CredentialException


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class UserExistsException(message : String, data : String = ""):RuntimeException(message)

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class UserNotFound(message: String) : RuntimeException(message)

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AccountExisits(message: String):RuntimeException(message)

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class AccountNotFound(message: String):RuntimeException(message)

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AccountRoleExists(message: String):RuntimeException(message)

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AccountRoleNotFound(message: String):RuntimeException(message)

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class CredentialsException(message: String) : CredentialException(message)