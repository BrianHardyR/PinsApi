package com.pins.api.utils

import com.pins.api.entities.auth.PinUserDetails
import org.springframework.security.core.context.SecurityContextHolder
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.*


// Time Extensions
fun Long.toLocalDate() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
fun Long.toLocalDateTime() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
fun Long.toLocalTime() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime()
fun LocalDate.toLong() = this.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
fun LocalDateTime.toLong() = this.atZone(ZoneId.of("GMT")).toInstant().toEpochMilli()
fun LocalTime.toLong() = this.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
fun now() = LocalDateTime.now(ZoneId.of("GMT"))



// String Extensions
fun String.clean() = "[^A-Za-z0-9 ]".toRegex().replace(this,"").toLowerCase().replace(" ","")

fun Number.format(digits: Int,decimal : Int = 0) = "%0${digits}.${decimal}f".format(this)

fun Double.round(places: Int): Double {
    require(places >= 0)
    var bd = BigDecimal.valueOf(this)
    bd = bd.setScale(places, RoundingMode.FLOOR)
    return bd.toDouble()
}

fun Long.round(places: Int): Double {
    require(places >= 0)
    var bd = BigDecimal.valueOf(this)
    bd = bd.setScale(places, RoundingMode.FLOOR)
    return bd.toDouble()
}

fun <T> safe(onError: (()->T)? = null , action : ()->T):T? =
    try {
        action()
    }catch (e:Exception){
        e.printStackTrace()
        print(e.message)
        onError?.invoke()
    }

fun getAccount() = (SecurityContextHolder.getContext().authentication.principal as PinUserDetails).account
fun getUser() = (SecurityContextHolder.getContext().authentication.principal as PinUserDetails).accountUser
fun getPrincipal() = (SecurityContextHolder.getContext().authentication.principal) as PinUserDetails