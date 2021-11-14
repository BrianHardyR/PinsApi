package com.pins.api.entities

import com.pins.api.utils.toLong
import java.time.LocalDateTime

open class Entity{
    open var createdAt : Long = LocalDateTime.now().toLong()
}