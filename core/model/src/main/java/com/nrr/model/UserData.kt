package com.nrr.model

data class UserData(
    val username: String,
    val languageConfig: LanguageConfig,
    val themeConfig: ThemeConfig,
    val dayNotificationOffset: TimeOffset,
    val weekNotificationOffset: TimeOffset,
    val monthNotificationOffset: TimeOffset,
    val pushNotification: PushNotificationConfig,
    val reminderQueue: List<TaskReminder>,
    val summariesGenerationReport: SummariesGenerationReport
)
