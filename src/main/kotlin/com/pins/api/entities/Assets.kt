package com.pins.api.entities

data class Assets(
        var id : Float,
        var url : String,
        var description : String,
        var content : Content? = null,
)