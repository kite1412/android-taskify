package com.nrr.model

data class UserData(
    val username: String,
    val languageConfig: LanguageConfig,
    val themeConfig: ThemeConfig,
    val dayNotificationOffset: NotificationOffset,
    val weekNotificationOffset: NotificationOffset,
    val monthNotificationOffset: NotificationOffset,
    val pushNotification: PushNotificationConfig,
    val reminderQueue: List<TaskReminder>
)
