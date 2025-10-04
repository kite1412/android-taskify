package com.nrr.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nrr.data.repository.TaskRepository
import com.nrr.data.repository.UserDataRepository
import com.nrr.model.LanguageConfig
import com.nrr.model.TimeOffset
import com.nrr.model.PushNotificationConfig
import com.nrr.model.ThemeConfig
import com.nrr.settings.navigation.SettingsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userDataRepository: UserDataRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {
    val routeMenu = savedStateHandle.toRoute<SettingsRoute>()
        .menuOrdinal
        ?.let { Menu.entries[it] }

    val userData = userDataRepository.userData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    internal var currentMenu by mutableStateOf<Menu?>(null)
        private set

    internal var taskReminders by mutableStateOf<List<ReminderInfo>?>(null)
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

    fun updateDayNotificationOffset(offset: TimeOffset) {
        viewModelScope.launch {
            userDataRepository.setDayNotificationOffsetConfig(offset)
        }
    }

    fun updateWeekNotificationOffset(offset: TimeOffset) {
        viewModelScope.launch {
            userDataRepository.setWeekNotificationOffsetConfig(offset)
        }
    }

    fun updateMonthNotificationOffset(offset: TimeOffset) {
        viewModelScope.launch {
            userDataRepository.setMonthNotificationOffsetConfig(offset)
        }
    }

    fun updateCurrentMenu(menu: Menu?) {
        currentMenu = menu
    }

    internal fun maybeLoadTaskReminders() {
        if (currentMenu == Menu.REMINDERS
            && taskReminders == null
            && userData.value != null
        ) {
            viewModelScope.launch {
                val rawReminders = userData.value!!.reminderQueue
                val tasks = taskRepository.getActiveTasksByIds(
                    activeTaskIds = rawReminders.map { it.activeTaskId }.distinct()
                ).firstOrNull()

                tasks?.let {
                    taskReminders = rawReminders.mapNotNull { reminder ->
                        tasks.find {
                            it.activeStatuses.any { s -> s.id == reminder.activeTaskId }
                        }?.let {
                            ReminderInfo.from(it, reminder)
                        }
                    }
                }
            }
        }
    }
}