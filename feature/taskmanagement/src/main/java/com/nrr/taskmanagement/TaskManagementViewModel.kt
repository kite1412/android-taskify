package com.nrr.taskmanagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.data.repository.TaskRepository
import com.nrr.domain.GetTasksByTitleUseCase
import com.nrr.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskManagementViewModel @Inject constructor(
    taskRepository: TaskRepository,
    private val getTasksByTitleUseCase: GetTasksByTitleUseCase
) : ViewModel() {
    val tasks = taskRepository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _filteredTasks = MutableStateFlow<List<Task>?>(null)
    val filteredTasks = _filteredTasks

    var searchValue by mutableStateOf("")
        private set

    var editMode by mutableStateOf(false)
        private set

    fun setSearchValue(value: String) {
        searchValue = value
    }

    fun setEditMode(value: Boolean) {
        editMode = value
    }

    fun clearSearchValue() {
        searchValue = ""
        _filteredTasks.value = null
    }

    fun searchTask() = viewModelScope.launch {
        _filteredTasks.value =
            getTasksByTitleUseCase(searchValue).firstOrNull() ?: emptyList()
    }
}