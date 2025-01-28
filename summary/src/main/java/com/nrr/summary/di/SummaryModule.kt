package com.nrr.summary.di

import com.nrr.summary.DefaultSummariesGenerationScheduler
import com.nrr.summary.SummariesGenerationScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SummaryModule {
    @Binds
    fun bindsSummaryGenerationScheduler(
        scheduler: DefaultSummariesGenerationScheduler
    ): SummariesGenerationScheduler
}