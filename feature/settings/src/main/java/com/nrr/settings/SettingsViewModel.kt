package com.nrr.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.data.repository.UserDataRepository
import com.nrr.model.LanguageConfig
import com.nrr.model.PushNotificationConfig
import com.nrr.model.ThemeConfig
import com.nrr.model.TimeUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    val userData = userDataRepository.userData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    internal var currentMenu by mutableStateOf<Menu?>(null)
        private set

    fun updateTheme(themeConfig: ThemeConfig) {
        viewModelScope.launch {
            userDataRepository.setThemeConfig(themeConfig)
        }
    }

    fun updateLanguage(languageConfig: LanguageConfig) {
        viewModelScope.launch {
            userDataRepository.setLanguageConfig(languageConfig)
        }
    }

    fun updatePushNotification(pushNotification: Boolean) {
        viewModelScope.launch {
            userDataRepository.setPushNotificationConfig(
                when (pushNotification) {
                    true -> PushNotificationConfig.PUSH_ALL
                    false -> PushNotificationConfig.PUSH_NONE
                }
            )
        }
    }

    fun updateDayTimeUnitChange(timeUnit: TimeUnit) {
        viewModelScope.launch {
            userData.value?.dayNotificationOffset?.let {
                userDataRepository.setDayNotificationOffsetConfig(
                    it.copy(timeUnit = timeUnit)
                )
            }
        }
    }

    fun updateDayOffsetChange(offset: Int) {
        viewModelScope.launch {
            userData.value?.dayNotificationOffset?.let {
                userDataRepository.setDayNotificationOffsetConfig(
                    it.copy(value = offset)
                )
            }
        }
    }

    fun updateWeekTimeUnitChange(timeUnit: TimeUnit) {
        viewModelScope.launch {
            userData.value?.weekNotificationOffset?.let {
                userDataRepository.setWeekNotificationOffsetConfig(
                    it.copy(timeUnit = timeUnit)
                )
            }
        }
    }

    fun updateWeekOffsetChange(offset: Int) {
        viewModelScope.launch {
            userData.value?.weekNotificationOffset?.let {
                userDataRepository.setWeekNotificationOffsetConfig(
                    it.copy(value = offset)
                )
            }
        }
    }

    fun updateMonthTimeUnitChange(timeUnit: TimeUnit) {
        viewModelScope.launch {
            userData.value?.monthNotificationOffset?.let {
                userDataRepository.setMonthNotificationOffsetConfig(
                    it.copy(timeUnit = timeUnit)
                )
            }
        }
    }

    fun updateMonthOffsetChange(offset: Int) {
        viewModelScope.launch {
            userData.value?.monthNotificationOffset?.let {
                userDataRepository.setMonthNotificationOffsetConfig(
                    it.copy(value = offset)
                )
            }
        }
    }

    internal fun updateCurrentMenu(menu: Menu?) {
        currentMenu = menu
    }
}