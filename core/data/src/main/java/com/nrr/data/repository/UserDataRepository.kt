package com.nrr.data.repository

import com.nrr.model.LanguageConfig
import com.nrr.model.NotificationOffset
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
}