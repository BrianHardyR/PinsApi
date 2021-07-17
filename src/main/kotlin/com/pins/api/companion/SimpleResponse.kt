package com.pins.api.companion

/**
 * This Class is used as a response object for apis
 */
class SimpleResponse<T>(
    val data: T,
    val code: Int, // HTTP Code
    val message: String,
)
