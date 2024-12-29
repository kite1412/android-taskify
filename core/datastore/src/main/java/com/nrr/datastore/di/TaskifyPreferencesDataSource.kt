package com.nrr.datastore.di

import android.util.Log
import androidx.datastore.core.DataStore
import com.nrr.datastore.LanguageConfigProto
import com.nrr.datastore.ThemeConfigProto
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

    suspend fun setLanguageConfig(newLanguageConfig: LanguageConfig) {
        try {
            userPreferences.updateData {
                it.copy {
                    languageConfig = when (newLanguageConfig) {
                        LanguageConfig.SYSTEM_DEFAULT -> LanguageConfigProto.LANGUAGE_CONFIG_PROTO_SYSTEM_DEFAULT
                        LanguageConfig.ENGLISH -> LanguageConfigProto.LANGUAGE_CONFIG_PROTO_ENGLISH
                        LanguageConfig.INDONESIAN -> LanguageConfigProto.LANGUAGE_CONFIG_PROTO_INDONESIAN
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating language config", e)
        }
    }

    suspend fun setThemeConfig(newThemeConfig: ThemeConfig) {
        try {
            userPreferences.updateData {
                it.copy {
                    themeConfig = when (newThemeConfig) {
                        ThemeConfig.SYSTEM_DEFAULT -> ThemeConfigProto.THEME_CONFIG_PROTO_SYSTEM_DEFAULT
                        ThemeConfig.LIGHT -> ThemeConfigProto.THEME_CONFIG_PROTO_LIGHT
                        ThemeConfig.DARK -> ThemeConfigProto.THEME_CONFIG_PROTO_DARK
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating theme config", e)
        }
    }
}