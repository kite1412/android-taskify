package com.nrr.taskmanagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.data.repository.TaskRepository
import com.nrr.domain.DeleteTasksUseCase
import com.nrr.domain.GetTasksByTitleUseCase
import com.nrr.domain.RemoveActiveTasksUseCase
import com.nrr.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskManagementViewModel @Inject constructor(
    taskRepository: TaskRepository,
    private val getTasksByTitleUseCase: GetTasksByTitleUseCase,
    private val removeActiveTasksUseCase: RemoveActiveTasksUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase
) : ViewModel() {
    val tasks = taskRepository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _searchTasks = MutableStateFlow<List<Task>?>(null)
    val searchTasks = _searchTasks.asStateFlow()

    private val _filteredTasks = MutableStateFlow<List<Task>?>(null)
    val filteredTasks = _filteredTasks.asStateFlow()

    val editedTasks = mutableStateListOf<Task>()

    var searchValue by mutableStateOf("")
        private set

    var editMode by mutableStateOf(false)
        private set

    var selectAll by mutableStateOf(false)
        private set

    var snackbarEvent by mutableStateOf("")
        private set

    internal val sortState by mutableStateOf(SortState())

    internal val filterState by mutableStateOf(FilterState())

    fun updateSearchValue(value: String) {
        searchValue = value
    }

    private fun updateEditMode(value: Boolean) {
        editMode = value
    }

    fun updateSelectAll(value: Boolean) {
        selectAll = value
    }

    fun updateSnackbarEvent(message: String) {
        snackbarEvent = message
    }

    fun clearSearch() {
        searchValue = ""
        _searchTasks.value = null
    }

    fun searchTask() {
        viewModelScope.launch {
            _searchTasks.update {
                getTasksByTitleUseCase(searchValue).firstOrNull() ?: emptyList()
            }
        }
    }

    fun updateEditedTasks(task: Task, checked: Boolean) {
        if (!editMode) updateEditMode(true)
        if (checked) editedTasks.add(task) else editedTasks.remove(task)
    }

    fun removeActiveTask(task: Task) {
        viewModelScope.launch {
            removeActiveTasksUseCase(listOf(task))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTasksUseCase(listOf(task))
        }
    }

    fun cancelEditMode() {
        updateEditMode(false)
        editedTasks.clear()
        updateSelectAll(false)
    }

    fun removeAllFromPlan(message: (Int) -> String) {
        viewModelScope.launch {
            removeActiveTasksUseCase(editedTasks)
            updateSnackbarEvent(message(editedTasks.size))
            cancelEditMode()
        }
    }

    fun deleteAllTasks(message: (Int) -> String) {
        viewModelScope.launch {
            deleteTasksUseCase(editedTasks)
            updateSnackbarEvent(message(editedTasks.size))
            cancelEditMode()
        }
    }

    internal fun onSort(type: Customize.Sort) {
        _filteredTasks.update {
            if (_searchTasks.value != null) _searchTasks.value!!.sort(type)
            else if (tasks.value != null) tasks.value!!.sort(type)
            else _filteredTasks.value
        }
    }

    internal fun onFilter(type: Customize.Filter) {
        _filteredTasks.update {
            if (_searchTasks.value != null) _searchTasks.value!!.filter(type)
            else if (tasks.value != null) tasks.value!!.filter(type)
            else _filteredTasks.value
        }
    }
}