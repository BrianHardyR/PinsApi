package com.pins.api.request_response.account

import com.pins.api.entities.auth.LinkType

data class LinkRequest(
    val userId : Long,
    val accountId : Long,
    val linkType : LinkType? = null
){
    fun valid(requireType : Boolean = true) : Boolean = userId >= 0 && accountId >= 0 && (if (requireType) linkType != null else true)
}
