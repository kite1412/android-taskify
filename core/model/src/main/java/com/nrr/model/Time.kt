package com.nrr.model

data class Time(
    val hour: Int,
    val minute: Int
) {
    override fun toString(): String =
        "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

fun String.toTime(): Time? {
    val s = split(":")
    return if (s.size == 2) {
        val hour = s[0].toIntOrNull()
        val minute = s[1].toIntOrNull()
        if (hour != null && minute != null) Time(hour, minute) else null
    } else null
}