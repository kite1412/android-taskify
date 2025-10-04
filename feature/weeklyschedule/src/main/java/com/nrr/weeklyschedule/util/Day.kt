package com.nrr.weeklyschedule.util
import com.nrr.weeklyschedule.util.WeeklyScheduleDictionary.friday
import com.nrr.weeklyschedule.util.WeeklyScheduleDictionary.monday
import com.nrr.weeklyschedule.util.WeeklyScheduleDictionary.saturday
import com.nrr.weeklyschedule.util.WeeklyScheduleDictionary.sunday
import com.nrr.weeklyschedule.util.WeeklyScheduleDictionary.thursday
import com.nrr.weeklyschedule.util.WeeklyScheduleDictionary.tuesday
import com.nrr.weeklyschedule.util.WeeklyScheduleDictionary.wednesday

internal enum class Day(val stringId: Int) {
    MONDAY(monday),
    TUESDAY(tuesday),
    WEDNESDAY(wednesday),
    THURSDAY(thursday),
    FRIDAY(friday),
    SATURDAY(saturday),
    SUNDAY(sunday)
}