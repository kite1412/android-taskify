package com.nrr.data.repository

import com.nrr.datastore.di.TaskifyPreferencesDataSource
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
}