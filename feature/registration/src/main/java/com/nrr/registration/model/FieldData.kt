package com.nrr.registration.model

import com.nrr.model.LanguageConfig
import com.nrr.model.ThemeConfig
import com.nrr.registration.util.RegistrationDictionary

internal data class FieldData(
    val stringId: Int,
    val options: List<String>,
    val onValueChange: (String) -> Unit,
    val mandatory: Boolean = false,
    val placeholder: String? = null
) {
    companion object {
        fun fieldData(
            onUsernameChange: (String) -> Unit,
            onLanguageChange: (String) -> Unit,
            onThemeChange: (String) -> Unit
        ) = listOf(
            FieldData(
                stringId = RegistrationDictionary.usernameQuestion,
                options = listOf(""),
                onValueChange = onUsernameChange,
                mandatory = true,
                placeholder = "Enter your username"
            ),
            FieldData(
                stringId = RegistrationDictionary.languageQuestion,
                options = ThemeConfig.entries.toList().map { it.toString() },
                onValueChange = onThemeChange
            ),
            FieldData(
                stringId = RegistrationDictionary.themeQuestion,
                options = LanguageConfig.entries.toList().map { it.toString() },
                onValueChange = onLanguageChange
            )
        )
    }
}