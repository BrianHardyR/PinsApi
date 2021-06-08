package com.pins.api.entities

data class Pins(
        var lat : Float,
        var lon : Float,
        var content : List<Content> = emptyList()
)