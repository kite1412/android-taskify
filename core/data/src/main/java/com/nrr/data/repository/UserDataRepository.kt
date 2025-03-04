package com.nrr.data.repository

import com.nrr.model.LanguageConfig
import com.nrr.model.NotificationOffset
import com.nrr.model.PushNotificationConfig
import com.nrr.model.SummariesGenerationReport
import com.nrr.model.TaskReminder
import com.nrr.model.ThemeConfig
import com.nrr.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>

    suspend fun setUsername(newUsername: String)

    suspend fun setLanguageConfig(newLanguageConfig: LanguageConfig)

    suspend fun setThemeConfig(newThemeConfig: ThemeConfig)

    suspend fun setDayNotificationOffsetConfig(newNotificationOffset: NotificationOffset)

    suspend fun setWeekNotificationOffsetConfig(newNotificationOffset: NotificationOffset)

    suspend fun setMonthNotificationOffsetConfig(newNotificationOffset: NotificationOffset)

    suspend fun setPushNotificationConfig(newPushNotificationConfig: PushNotificationConfig)

    suspend fun addTaskReminders(reminders: Map<Int, TaskReminder>)

    suspend fun removeTaskReminders(indexes: List<Int>)

    suspend fun removeAllTaskReminders()

    suspend fun setSummariesGenerationReport(report: SummariesGenerationReport)
}