package com.nrr.datastore

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TaskifyPreferencesDataSourceTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val scope = TestScope(UnconfinedTestDispatcher())
    private lateinit var dataSource: TaskifyPreferencesDataSource

    @Before
    fun initDataSource() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dataSource = TaskifyPreferencesDataSource(
            userPreferences = DataStoreFactory.create(
                serializer = UserPreferencesSerializer(),
                scope = scope
            ) {
                context.dataStoreFile("user_preferences.pb")
            }
        )
    }

    @Test
    fun usernameShouldEmpty() = runTest {
        assert(dataSource.userData.first().username.isEmpty())
    }

    @Test
    fun readAndWrite_usernameShouldNotMatch() = runTest {
        dataSource.setUsername("test")
        assert(dataSource.userData.first().username != "a")
    }
}