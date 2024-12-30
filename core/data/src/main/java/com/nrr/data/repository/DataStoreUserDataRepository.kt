package com.nrr.data.repository

import com.nrr.datastore.di.TaskifyPreferencesDataSource
import com.nrr.model.LanguageConfig
import com.nrr.model.ThemeConfig
import com.nrr.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreUserDataRepository @Inject constructor(
    private val taskifyPreferencesDataSource: TaskifyPreferencesDataSource
) : UserDataRepository {
    override val userData: Flow<UserData> =
        taskifyPreferencesDataSource.userData

    override suspend fun setUsername(newUsername: String) =
        taskifyPreferencesDataSource.setUsername(newUsername)

    override suspend fun setLanguageConfig(newLanguageConfig: LanguageConfig) =
        taskifyPreferencesDataSource.setLanguageConfig(newLanguageConfig)

    override suspend fun setThemeConfig(newThemeConfig: ThemeConfig) =
        taskifyPreferencesDataSource.setThemeConfig(newThemeConfig)
}