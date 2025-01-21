package com.nrr.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nrr.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    internal var currentMenu by mutableStateOf<Menu?>(null)
        private set

    internal fun updateCurrentMenu(menu: Menu) {
        currentMenu = menu
    }
}