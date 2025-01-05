package com.nrr.taskdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import com.nrr.model.TaskType
import com.nrr.taskdetail.navigation.TaskDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository
) : ViewModel() {
    val taskId = savedStateHandle.toRoute<TaskDetailRoute>().taskId

    var task by mutableStateOf<Task?>(null)
        private set

    var editMode by mutableStateOf(false)
        private set

    var showWarning by mutableStateOf(false)
        private set

    internal var editedTask by mutableStateOf(TaskEdit())
        private set

    init {
        viewModelScope.launch {
            taskId?.let {
                taskRepository.getByIds(listOf(it)).collect { l ->
                    l[0].also { t ->
                        task = t
                        editedTask = t.toTaskEdit()
                    }
                }
            }
        }
    }

    fun updateTitle(title: String) {
        editedTask = editedTask.copy(title = title)
    }

    fun updateDescription(description: String) {
        editedTask = editedTask.copy(description = description)
    }

    fun updateType(type: TaskType) {
        editedTask = editedTask.copy(taskType = type)
    }

    fun cancelEditMode() {
        if (showWarning) {

            showWarning = false
        } else {
            showWarning = false
            editedTask = task?.toTaskEdit() ?: TaskEdit()
            this.editMode = false
        }
    }
}