package com.pins.api.entities

data class Content(
        var id : Float,
        var content : String,
        var assets : List<Assets> = emptyList(),
        var pins : List<Pins> = emptyList(),
)