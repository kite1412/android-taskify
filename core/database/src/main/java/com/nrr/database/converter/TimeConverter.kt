package com.nrr.database.converter

import androidx.room.TypeConverter
import com.nrr.model.Time
import com.nrr.model.toTime

internal class TimeConverter {
    @TypeConverter
    fun stringToTime(value: String?): Time? =
        value?.toTime()

    @TypeConverter
    fun timeToString(time: Time?): String? =
        time?.toString()
}