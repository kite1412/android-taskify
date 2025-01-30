package com.nrr.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.data.repository.UserDataRepository
import com.nrr.model.LanguageConfig
import com.nrr.model.ThemeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    var username by mutableStateOf("")
        private set

    var languageConfig by mutableStateOf(LanguageConfig.SYSTEM_DEFAULT)
        private set

    var themeConfig by mutableStateOf(ThemeConfig.SYSTEM_DEFAULT)
        private set

    fun updateUsername(username: String) {
        if (username.length <= 20) this.username = username
    }

    fun updateLanguageConfig(languageConfig: String) {
        this.languageConfig = LanguageConfig.fromString(languageConfig)
    }

    fun updateThemeConfig(themeConfig: String) {
        this.themeConfig = ThemeConfig.fromString(themeConfig)
    }

    internal fun register() = viewModelScope.launch {
        userDataRepository.setLanguageConfig(languageConfig)
        userDataRepository.setThemeConfig(themeConfig)
        userDataRepository.setUsername(username)
    }
}