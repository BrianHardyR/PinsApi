package com.pins.api.request_response.auth

import com.pins.api.entities.auth.AuthProviderType


data class AuthRequest(
    var type: AuthProviderType,
    var identifier : String,
    var secret : String = ""
){
    fun valid() = identifier.isNotEmpty()
}