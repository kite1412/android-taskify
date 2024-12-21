package com.nrr.model

data class Time(
    val hour: Int,
    val minute: Int
) {
    override fun toString(): String =
        "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}