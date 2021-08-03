package com.pins.api.utils

import com.pins.api.security.AppUserDetails
import org.springframework.security.core.context.SecurityContextHolder
import java.time.*

// Time Extensions
fun Long.toLocalDate() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
fun Long.toLocalDateTime() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
fun Long.toLocalTime() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime()
fun LocalDate.toLong() = this.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
fun LocalDateTime.toLong() = this.atZone(ZoneId.of("GMT")).toInstant().toEpochMilli()
fun LocalTime.toLong() = this.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
fun now() = LocalDateTime.now(ZoneId.of("GMT"))

// User Security context
fun getLoggedInUserDetails() = SecurityContextHolder.getContext().authentication.principal as AppUserDetails


// String Extensions
fun String.clean() = "[^A-Za-z0-9 ]".toRegex().replace(this,"").toLowerCase().replace(" ","")

fun Number.format(digits: Int,decimal : Int = 0) = "%0${digits}.${decimal}f".format(this)