package com.nrr.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nrr.data.repository.UserDataRepository
import com.nrr.model.LanguageConfig
import com.nrr.model.ThemeConfig
import com.nrr.registration.model.FieldAction
import com.nrr.registration.model.FieldData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    var username by mutableStateOf("")
        private set

    private var languageConfig by mutableStateOf(LanguageConfig.SYSTEM_DEFAULT)

    private var themeConfig by mutableStateOf(ThemeConfig.SYSTEM_DEFAULT)

    internal val fieldData = FieldData.fieldData(
        onUsernameChange = { username = it },
        onLanguageChange = { languageConfig = LanguageConfig.fromString(it) },
        onThemeChange = { themeConfig = ThemeConfig.fromString(it) }
    )

    internal fun onAction(action: FieldAction) {

    }

    fun register() {

    }
}