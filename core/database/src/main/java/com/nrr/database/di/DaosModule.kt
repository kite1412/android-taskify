package com.nrr.database.di

import com.nrr.database.TaskifyDatabase
import com.nrr.database.dao.ActiveTaskDao
import com.nrr.database.dao.ActiveTaskSummaryDao
import com.nrr.database.dao.SummaryGroupDao
import com.nrr.database.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesTaskDao(
        database: TaskifyDatabase,
    ): TaskDao = database.taskDao()

    @Provides
    fun providesActiveTaskDao(
        database: TaskifyDatabase,
    ): ActiveTaskDao = database.activeTaskDao()

    @Provides
    fun providesActiveTaskSummaryDao(
        database: TaskifyDatabase,
    ): ActiveTaskSummaryDao = database.activeTaskSummaryDao()

    @Provides
    fun providesSummaryGroupDao(
        database: TaskifyDatabase,
    ): SummaryGroupDao = database.summaryGroupDao()
}