package com.nrr.model

enum class ThemeConfig {
    SYSTEM_DEFAULT,
    LIGHT,
    DARK;

    companion object {
        fun fromString(value: String): ThemeConfig {
            return when (value) {
                "System Default" -> SYSTEM_DEFAULT
                "Light" -> LIGHT
                else -> DARK
            }
        }
    }


    override fun toString(): String {
        return when (this) {
            SYSTEM_DEFAULT -> "System Default"
            LIGHT -> "Light"
            else -> "Dark"
        }
    }

    fun toStringIn(): String {
        return when (this) {
            SYSTEM_DEFAULT -> "Default Sistem"
            LIGHT -> "Gelap"
            else -> "Terang"
        }
    }
}