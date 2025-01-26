package com.nrr.notification.di

import com.nrr.notification.AlarmManagerScheduledTaskNotifier
import com.nrr.notification.ScheduledTaskNotifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface NotificationModule {
    @Binds
    fun bindsScheduledTaskNotifier(
        scheduledTaskNotifier: AlarmManagerScheduledTaskNotifier
    ): ScheduledTaskNotifier
}