package com.nrr.data.di

import com.nrr.data.repository.RoomTaskRepository
import com.nrr.data.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    abstract fun bindsTaskRepository(
        taskRepository: RoomTaskRepository
    ): TaskRepository
}