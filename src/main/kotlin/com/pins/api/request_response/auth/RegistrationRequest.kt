package com.pins.api.request_response.auth

data class RegistrationRequest(
    val username : String,
    val credential : AuthRequest
){
    fun valid() = username.isNotEmpty() && credential.valid()
}