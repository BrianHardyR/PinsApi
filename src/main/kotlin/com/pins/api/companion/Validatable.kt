package com.pins.api.companion



/**
 * Requires implementation of a 'valid' function which return a boolean
 * This interface offers a streamlined way to check if an object is valid
 */
interface Validatable {
    abstract fun valid() : Boolean
}