package com.nrr.data.di

import com.nrr.data.repository.DataStoreUserDataRepository
import com.nrr.data.repository.RoomTaskRepository
import com.nrr.data.repository.TaskRepository
import com.nrr.data.repository.UserDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    fun bindsTaskRepository(
        taskRepository: RoomTaskRepository
    ): TaskRepository

    @Binds
    fun bindsUserDataRepository(
        userDataRepository: DataStoreUserDataRepository
    ): UserDataRepository
}