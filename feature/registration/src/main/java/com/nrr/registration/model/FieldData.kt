package com.nrr.registration.model

import com.nrr.model.LanguageConfig
import com.nrr.model.ThemeConfig
import com.nrr.registration.util.RegistrationDictionary

internal data class FieldData(
    val stringId: Int,
    val currentValue: String,
    val options: List<String>,
    val onValueChange: (String) -> Unit,
    val placeholder: String? = null
) {
    companion object {
        fun fieldsData(
            username: String,
            languageConfig: LanguageConfig,
            themeConfig: ThemeConfig,
            onUsernameChange: (String) -> Unit,
            onLanguageChange: (String) -> Unit,
            onThemeChange: (String) -> Unit
        ) = listOf(
            FieldData(
                stringId = RegistrationDictionary.usernameQuestion,
                currentValue = username,
                options = listOf(username),
                onValueChange = onUsernameChange,
                placeholder = "Enter your username"
            ),
            FieldData(
                stringId = RegistrationDictionary.themeQuestion,
                currentValue = themeConfig.toString(),
                options = ThemeConfig.entries.map { it.toString() },
                onValueChange = onThemeChange
            ),
            FieldData(
                stringId = RegistrationDictionary.languageQuestion,
                currentValue = languageConfig.toString(),
                options = LanguageConfig.entries.map { it.toString() },
                onValueChange = onLanguageChange
            )
        )
    }
}