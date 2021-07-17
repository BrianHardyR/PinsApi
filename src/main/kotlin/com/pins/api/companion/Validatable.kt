package com.pins.api.companion

import jdk.nashorn.internal.ir.annotations.Ignore

/**
 * Requires implementation of a 'valid' function which return a boolean
 * This interface offers a streamlined way to check if an object is valid
 */
interface Validatable {
    @Ignore
    abstract fun valid() : Boolean
}