package com.nrr.database.di

import android.content.Context
import androidx.room.Room
import com.nrr.database.TaskifyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesTaskifyDatabase(
        @ApplicationContext context: Context
    ): TaskifyDatabase = Room.databaseBuilder(
        context,
        TaskifyDatabase::class.java,
        "taskify-database"
    ).build()
}