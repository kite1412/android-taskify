package com.nrr.database.di

import com.nrr.database.TaskifyDatabase
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
}