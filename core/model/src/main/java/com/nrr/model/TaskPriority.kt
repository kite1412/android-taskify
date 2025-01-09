package com.nrr.model

enum class TaskPriority {
    NORMAL,
    HIGH,
    CRITICAL;

    override fun toString() = when (this) {
        NORMAL -> "Normal"
        HIGH -> "High"
        CRITICAL -> "Critical"
    }

    fun toStringIn() = when (this) {
        NORMAL -> "Normal"
        HIGH -> "Tinggi"
        CRITICAL -> "Kritis"
    }
}