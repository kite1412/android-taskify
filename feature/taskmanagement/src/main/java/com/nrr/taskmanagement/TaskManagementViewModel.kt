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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskManagementViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val getTasksByTitleUseCase: GetTasksByTitleUseCase,
    private val removeActiveTasksUseCase: RemoveActiveTasksUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>?>(null)
    val tasks = _tasks.asStateFlow()

    private val _searchTasks = MutableStateFlow<List<Task>?>(null)
    val searchTasks = _searchTasks.asStateFlow()

    // unmodifiable tasks
    private var allTasks = emptyList<Task>()
    private var allSearchTasks = emptyList<Task>()

    val editedTasks = mutableStateListOf<Task>()

    var searchValue by mutableStateOf("")
        private set

    var editMode by mutableStateOf(false)
        private set

    var selectAll by mutableStateOf(false)
        private set

    var snackbarEvent by mutableStateOf("")
        private set

    internal val sortState = SortState()

    internal val filterState = FilterState()

    internal var confirmation by mutableStateOf<ConfirmationType?>(null)
        private set

    init {
        viewModelScope.launch {
            taskRepository.getTasks().collect {
                allTasks = it
                _tasks.update {
                    allTasks.sortAndFilter(
                        sortType = sortState.selected,
                        filterType = filterState.selected
                    )
                }
                if (searchTasks.value != null) {
                    allSearchTasks = allSearchTasks.filter { t ->
                        t in allTasks
                    }
                    _searchTasks.update {
                        allSearchTasks.sortAndFilter(
                            sortType = sortState.selected,
                            filterType = filterState.selected
                        )
                    }
                }
            }
        }
    }

    private fun updateEditMode(value: Boolean) {
        editMode = value
    }

    private fun removeAllFromPlan(message: (Int) -> String) {
        if (confirmation == null) confirmation = ConfirmationType.REMOVE_ALL
        else {
            viewModelScope.launch {
                removeActiveTasksUseCase(editedTasks)
                updateSnackbarEvent(message(confirmation?.totalAffected ?: 0))
                cancelEditMode()
                dismissConfirmation()
            }
        }
    }

    private fun deleteAllTasks(message: (Int) -> String) {
        if (confirmation == null) confirmation = ConfirmationType.DELETE_ALL
        else {
            viewModelScope.launch {
                deleteTasksUseCase(editedTasks)
                updateSnackbarEvent(message(confirmation?.totalAffected ?: 0))
                cancelEditMode()
                dismissConfirmation()
            }
        }
    }

    fun updateSearchValue(value: String) {
        searchValue = value
    }

    fun updateSelectAll(value: Boolean) {
        selectAll = value
        if (value) {
            editedTasks.apply {
                clear()
                addAll(
                    searchTasks.value
                        ?: tasks.value
                        ?: emptyList()
                )
            }
        } else editedTasks.clear()
    }

    fun updateSnackbarEvent(message: String) {
        snackbarEvent = message
    }

    fun clearSearch() {
        searchValue = ""
        _searchTasks.update { null }
        // to sync sort type from search
        _tasks.update {
            allTasks.sortAndFilter(
                sortType = sortState.selected,
                filterType = filterState.selected
            )
        }
    }

    fun searchTask() {
        viewModelScope.launch {
            allSearchTasks = getTasksByTitleUseCase(searchValue)
                .firstOrNull() ?: emptyList()
            _searchTasks.update {
                allSearchTasks.sortAndFilter(
                    sortType = sortState.selected,
                    filterType = filterState.selected
                )
            }
        }
    }

    fun updateEditedTasks(task: Task, checked: Boolean) {
        if (!editMode) updateEditMode(true)
        if (checked) {
            editedTasks.add(task)
            if (editedTasks.size == (searchTasks.value?.size ?: tasks.value?.size))
                selectAll = true
        } else {
            editedTasks.remove(task)
            selectAll = false
        }
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

    fun removeAllConfirmation() {
        confirmation = ConfirmationType.REMOVE_ALL
        confirmation!!.updateTotalAffected(
            value = editedTasks.filter { it.activeStatuses.isNotEmpty() }.size
        )
    }

    fun deleteAllConfirmation() {
        confirmation = ConfirmationType.DELETE_ALL
        confirmation!!.updateTotalAffected(editedTasks.size)
    }

    fun dismissConfirmation() {
        confirmation = null
    }

    internal fun onSort(type: Customize.Sort) {
        val state = _searchTasks.takeIf { it.value != null } ?: _tasks
        state.update {
            state.value?.sort(type)
        }
    }

    internal fun onFilter(type: Customize.Filter) {
        var tasks = allTasks
        val state = _searchTasks
            .takeIf {
                (it.value != null).also { t ->
                    if (t) tasks = allSearchTasks
                }
            } ?: _tasks

        state.update {
            tasks.sortAndFilter(
                sortType = sortState.selected,
                filterType = type
            )
        }
    }

    internal fun handleConfirmation(
        type: ConfirmationType,
        message: (Int) -> String
    ) {
        when (type) {
            ConfirmationType.REMOVE_ALL -> removeAllFromPlan(message)
            ConfirmationType.DELETE_ALL -> deleteAllTasks(message)
        }
    }
}