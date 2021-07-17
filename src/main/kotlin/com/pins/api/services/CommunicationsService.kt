package com.pins.api.services

import org.springframework.stereotype.Service

/**
 * Handle all communication 'UserEngagment' related functions
 * From Sending Email to recieving feadback
 */

@Service
class CommunicationsService {

    fun sendEmail(htmlMessage : String){}

    fun sendPush(message : String){}

    fun sendSms(message : String){}

}