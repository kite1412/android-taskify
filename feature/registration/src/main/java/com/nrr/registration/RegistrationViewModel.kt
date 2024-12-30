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

    private var languageConfig by mutableStateOf(LanguageConfig.SYSTEM_DEFAULT)

    private var themeConfig by mutableStateOf(ThemeConfig.SYSTEM_DEFAULT)

    fun setUserName(username: String) {
        if (username.length <= 20) this.username = username
    }

    fun setLanguageConfig(languageConfig: String) {
        this.languageConfig = LanguageConfig.fromString(languageConfig)
    }

    fun setThemeConfig(themeConfig: String) {
        this.themeConfig = ThemeConfig.fromString(themeConfig)
    }

    internal fun register() = viewModelScope.launch {
        userDataRepository.setUsername(username)
        userDataRepository.setLanguageConfig(languageConfig)
        userDataRepository.setThemeConfig(themeConfig)
    }
}