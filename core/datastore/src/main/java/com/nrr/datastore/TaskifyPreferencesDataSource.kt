package com.nrr.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.nrr.datastore.util.toTimeUnit
import com.nrr.datastore.util.toTimeUnitProto
import com.nrr.model.LanguageConfig
import com.nrr.model.NotificationOffset
import com.nrr.model.PushNotificationConfig
import com.nrr.model.ReminderType
import com.nrr.model.TaskReminder
import com.nrr.model.ThemeConfig
import com.nrr.model.TimeUnit
import com.nrr.model.UserData
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import javax.inject.Inject

class TaskifyPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    private val tag = "TaskifyDataSource"

    val userData = userPreferences.data
        .map {
            UserData(
                username = it.username,
                languageConfig = LanguageConfig.entries[it.languageConfig.ordinal],
                themeConfig = ThemeConfig.entries[it.themeConfig.ordinal],
                dayNotificationOffset = with(it.dayNotificationOffset) {
                    NotificationOffset(
                        value = value.takeIf { v -> v > 0 } ?: 1,
                        timeUnit =
                            timeUnit.takeIf { tu -> tu != TimeUnitProto.TIME_UNIT_PROTO_DAYS }?.toTimeUnit()
                                ?: TimeUnit.MINUTES
                    )
                },
                weekNotificationOffset = with(it.weekNotificationOffset) {
                    NotificationOffset(
                        value = value.takeIf { v -> v > 0 } ?: 1,
                        timeUnit = timeUnit.toTimeUnit()
                    )
                },
                monthNotificationOffset = with(it.monthNotificationOffset) {
                    NotificationOffset(
                        value = value.takeIf { v -> v > 0 } ?: 1,
                        timeUnit = timeUnit.toTimeUnit()
                    )
                },
                pushNotification = PushNotificationConfig.entries[it.pushNotification.ordinal],
                reminderQueue = it.reminderQueueList.map { r ->
                    TaskReminder(
                        activeTaskId = r.activeTaskId,
                        reminderType = ReminderType.entries[r.reminderType.ordinal],
                        date = Instant.fromEpochMilliseconds(r.epochMillis)
                    )
                }
            )
        }

    suspend fun setUsername(newUsername: String) {
        try {
            userPreferences.updateData {
                it.copy {
                    username = newUsername
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating username", e)
        }
    }

    suspend fun setLanguageConfig(newLanguageConfig: LanguageConfig) {
        try {
            userPreferences.updateData {
                it.copy {
                    languageConfig = when (newLanguageConfig) {
                        LanguageConfig.SYSTEM_DEFAULT -> LanguageConfigProto.LANGUAGE_CONFIG_PROTO_SYSTEM_DEFAULT
                        LanguageConfig.ENGLISH -> LanguageConfigProto.LANGUAGE_CONFIG_PROTO_ENGLISH
                        LanguageConfig.INDONESIAN -> LanguageConfigProto.LANGUAGE_CONFIG_PROTO_INDONESIAN
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating language config", e)
        }
    }

    suspend fun setThemeConfig(newThemeConfig: ThemeConfig) {
        try {
            userPreferences.updateData {
                it.copy {
                    themeConfig = when (newThemeConfig) {
                        ThemeConfig.SYSTEM_DEFAULT -> ThemeConfigProto.THEME_CONFIG_PROTO_SYSTEM_DEFAULT
                        ThemeConfig.LIGHT -> ThemeConfigProto.THEME_CONFIG_PROTO_LIGHT
                        ThemeConfig.DARK -> ThemeConfigProto.THEME_CONFIG_PROTO_DARK
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating theme config", e)
        }
    }

    suspend fun setDayNotificationOffsetConfig(newNotificationOffset: NotificationOffset) {
        try {
            userPreferences.updateData {
                it.copy {
                    dayNotificationOffset = with(newNotificationOffset) {
                        NotificationOffsetConfigProto.newBuilder()
                            .setValue(value)
                            .setTimeUnit(timeUnit.toTimeUnitProto())
                            .build()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating day notification offset config", e)
        }
    }

    suspend fun setWeekNotificationOffsetConfig(newNotificationOffset: NotificationOffset) {
        try {
            userPreferences.updateData {
                it.copy {
                    weekNotificationOffset = with(newNotificationOffset) {
                        NotificationOffsetConfigProto.newBuilder()
                            .setValue(value)
                            .setTimeUnit(timeUnit.toTimeUnitProto())
                            .build()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating week notification offset config", e)
        }
    }

    suspend fun setMonthNotificationOffsetConfig(newNotificationOffset: NotificationOffset) {
        try {
            userPreferences.updateData {
                it.copy {
                    monthNotificationOffset = with(newNotificationOffset) {
                        NotificationOffsetConfigProto.newBuilder()
                            .setValue(value)
                            .setTimeUnit(timeUnit.toTimeUnitProto())
                            .build()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating month notification offset config", e)
        }
    }

    suspend fun setPushNotificationConfig(newPushNotificationConfig: PushNotificationConfig) {
        try {
            userPreferences.updateData {
                it.copy {
                    pushNotification = when (newPushNotificationConfig) {
                        PushNotificationConfig.PUSH_ALL -> PushNotificationConfigProto.PUSH_NOTIFICATION_PROTO_PUSH_ALL
                        PushNotificationConfig.PUSH_NONE -> PushNotificationConfigProto.PUSH_NOTIFICATION_PROTO_PUSH_NONE
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating push notification config", e)
        }
    }
}