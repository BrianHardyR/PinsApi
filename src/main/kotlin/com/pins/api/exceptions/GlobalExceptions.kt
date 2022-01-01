package com.pins.api.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.UNAUTHORIZED)
class AuthException:RuntimeException("Authentication Error")

@ResponseStatus(HttpStatus.NOT_FOUND)
class AccountNotFound:RuntimeException("Account not found")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidRequest:RuntimeException("Malformed request please check request and try again")

@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
class NotSupported:RuntimeException("We do not support this type of request yet :(")

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFound:RuntimeException("User not found please register")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class FileUploadError : RuntimeException("File upload error")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class FileNotFound:RuntimeException("Media not found")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class PostNotFound:RuntimeException("Parent comment not found")