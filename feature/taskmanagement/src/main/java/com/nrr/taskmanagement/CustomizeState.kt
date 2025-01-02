package com.nrr.taskmanagement
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal open class CustomizeState<T : Customize>(
    selected: T,
    expanded: Boolean,
    val options: List<T>
) {
    var selected by mutableStateOf(selected)
        private set

    var expanded by mutableStateOf(expanded)
        private set

    fun expandDropdown() {
        expanded = true
    }

    fun dismissDropdown() {
        expanded = false
    }

    fun select(item: T) {
        selected = item
    }
}