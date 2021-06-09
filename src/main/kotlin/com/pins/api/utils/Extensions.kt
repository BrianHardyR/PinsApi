package com.pins.api.utils

import java.time.*


fun Long.toLocalDate() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
fun Long.toLocalDateTime() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
fun Long.toLocalTime() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime()
fun LocalDate.toLong() = this.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
fun LocalDateTime.toLong() = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
fun LocalTime.toLong() = this.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
