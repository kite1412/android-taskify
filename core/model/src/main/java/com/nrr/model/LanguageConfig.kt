package com.nrr.model

enum class LanguageConfig {
    SYSTEM_DEFAULT,
    ENGLISH,
    INDONESIAN;

    companion object {
        fun fromString(value: String): LanguageConfig {
            return when (value) {
                "System Default" -> SYSTEM_DEFAULT
                "English" -> ENGLISH
                else -> INDONESIAN
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            SYSTEM_DEFAULT -> "System Default"
            ENGLISH -> "English"
            else -> "Indonesia"
        }
    }

    fun toCode(): String = when (this) {
        SYSTEM_DEFAULT -> "en"
        ENGLISH -> "en"
        else -> "in"
    }
}