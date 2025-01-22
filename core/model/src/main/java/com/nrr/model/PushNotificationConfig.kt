package com.nrr.model

enum class PushNotificationConfig {
    PUSH_ALL,
    PUSH_NONE;

    override fun toString(): String {
        return when (this) {
            PUSH_ALL -> "On"
            else -> "Off"
        }
    }
}