package com.nrr.model

enum class TaskType {
    PERSONAL,
    WORK,
    LEARNING,
    HEALTH,
    REFLECTION,
    SPECIAL;

    override fun toString(): String = when (this) {
        PERSONAL -> "Personal"
        WORK -> "Work"
        LEARNING -> "Learning"
        HEALTH -> "Health"
        REFLECTION -> "Reflection"
        SPECIAL -> "Special"
    }

    fun toStringIn() = when (this) {
        PERSONAL -> "Pribadi"
        WORK -> "Kerja"
        LEARNING -> "Belajar"
        HEALTH -> "Kesehatan"
        REFLECTION -> "Refleksi"
        SPECIAL -> "Spesial"
    }
}