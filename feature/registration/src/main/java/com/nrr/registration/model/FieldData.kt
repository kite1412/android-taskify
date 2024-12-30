package com.nrr.registration.model

import com.nrr.model.LanguageConfig
import com.nrr.model.ThemeConfig
import com.nrr.registration.util.RegistrationDictionary

internal data class FieldData(
    val stringId: Int,
    val options: List<String>,
    val onValueChange: (String) -> Unit,
    val placeholder: String? = null
) {
    companion object {
        fun fieldData(
            username: String,
            onUsernameChange: (String) -> Unit,
            onLanguageChange: (String) -> Unit,
            onThemeChange: (String) -> Unit
        ) = listOf(
            FieldData(
                stringId = RegistrationDictionary.usernameQuestion,
                options = listOf(username),
                onValueChange = onUsernameChange,
                placeholder = "Enter your username"
            ),
            FieldData(
                stringId = RegistrationDictionary.themeQuestion,
                options = ThemeConfig.entries.toList().map { it.toString() },
                onValueChange = onThemeChange
            ),
            FieldData(
                stringId = RegistrationDictionary.languageQuestion,
                options = LanguageConfig.entries.toList().map { it.toString() },
                onValueChange = onLanguageChange
            )
        )
    }
}