package com.nrr.datastore.di

import android.util.Log
import androidx.datastore.core.DataStore
import com.nrr.datastore.UserPreferences
import com.nrr.datastore.copy
import com.nrr.model.LanguageConfig
import com.nrr.model.ThemeConfig
import com.nrr.model.UserData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskifyPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    private val tag = "TaskifyDataSource"

    val userData = userPreferences.data
        .map {
            UserData(
                username = it.username,
                languageConfig = LanguageConfig.entries[it.languageConfig.ordinal],
                themeConfig = ThemeConfig.entries[it.themeConfig.ordinal]
            )
        }

    suspend fun setUsername(newUsername: String) {
        try {
            userPreferences.updateData {
                it.copy {
                    username = newUsername
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating username", e)
        }
    }
}