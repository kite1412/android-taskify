package com.nrr.taskdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nrr.data.repository.TaskRepository
import com.nrr.domain.SaveTasksUseCase
import com.nrr.model.Task
import com.nrr.model.TaskType
import com.nrr.taskdetail.navigation.TaskDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val saveTasksUseCase: SaveTasksUseCase
) : ViewModel() {
    val taskId = savedStateHandle.toRoute<TaskDetailRoute>().taskId

    var task by mutableStateOf<Task?>(null)
        private set

    var editMode by mutableStateOf(false)
        private set

    internal var confirmation by mutableStateOf<ConfirmationType?>(null)
        private set

    internal var editedTask by mutableStateOf(task?.toTaskEdit() ?: TaskEdit())
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

    fun updateEditMode(value: Boolean) {
        editMode = value
    }

    fun dismissConfirmation() {
        confirmation = null
    }

    fun cancelEditMode() {
        if (confirmation == null) {
            if (editedTask.title != task?.title
                || editedTask.description != task?.description
                || editedTask.taskType != task?.taskType
            ) {
                confirmation = ConfirmationType.CANCEL_EDIT
            } else updateEditMode(false)
        } else {
            dismissConfirmation()
            editedTask = task?.toTaskEdit() ?: TaskEdit()
            updateEditMode(false)
        }
    }

    suspend fun saveEdit() {
        val task = editedTask.toTask().copy(
            description = editedTask.description.ifEmpty { null },
            createdAt = task?.createdAt ?: Clock.System.now()
        )
        this.task = task
        saveTasksUseCase(listOf(task))
        updateEditMode(false)
    }

    internal fun handleConfirmation(type: ConfirmationType) {
        when (type) {
            ConfirmationType.CANCEL_EDIT -> cancelEditMode()
        }
    }
}