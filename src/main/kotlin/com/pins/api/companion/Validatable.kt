package com.pins.api.companion

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Requires implementation of a 'valid' function which return a boolean
 * This interface offers a streamlined way to check if an object is valid
 */
interface Validatable {
    @JsonIgnore
    abstract fun valid() : Boolean
}