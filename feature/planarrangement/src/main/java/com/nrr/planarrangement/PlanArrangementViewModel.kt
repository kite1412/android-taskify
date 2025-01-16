package com.nrr.planarrangement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nrr.data.repository.TaskRepository
import com.nrr.domain.SaveActiveTasksUseCase
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskPriority
import com.nrr.planarrangement.navigation.PlanArrangementRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanArrangementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val saveActiveTasksUseCase: SaveActiveTasksUseCase
) : ViewModel() {
    // navigation from non-null ActiveStatus to be used in TaskEdit typically for
    // editing existing ActiveStatus
    // if not null navigate back to calling route instead of making TaskEdit null
    private val activeStatusId = savedStateHandle.toRoute<PlanArrangementRoute>().activeStatusId

    // navigation from nullable ActiveStatus typically for creating a new ActiveStatus
    // if not null navigate back to calling route instead of making TaskEdit null
    private val taskId = savedStateHandle.toRoute<PlanArrangementRoute>().taskId

    val immediatePopBackStack = activeStatusId != null || taskId != null

    val tasks = if (activeStatusId == null && taskId == null) taskRepository.getTasks()
        .map {
            it.sortedByDescending { t -> t.updateAt }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        ) else flowOf(null)

    var period by mutableStateOf<TaskPeriod?>(
        savedStateHandle.toRoute<PlanArrangementRoute>().periodOrdinal?.let { TaskPeriod.entries[it] }
    )
        private set

    var assigningTask by mutableStateOf(false)
        private set

    internal var taskEdit by mutableStateOf<TaskEdit?>(null)
        private set

    internal val saveEnabled = snapshotFlow { taskEdit }
        .map {
            it?.selectedStartDate != null
                && (it.task.activeStatuses.firstOrNull { s ->
                    s.id == it.activeStatus.id
                }?.let { status ->
                    status.period != it.activeStatus.period
                        || status.priority != it.activeStatus.priority
                        || status.reminderSet != it.activeStatus.reminderSet
                        || status.isDefault != it.activeStatus.isDefault
                        || status.startDate.toDate() != it.selectedStartDate
                        || status.dueDate?.toDate() != it.selectedDueDate
                } ?: true)
                && (it.selectedDueDate == null || it.selectedStartDate <= it.selectedDueDate)
                && (it.activeStatus.period == TaskPeriod.DAY || it.selectedStartDate.dayOfMonth != null)
        }

    init {
        viewModelScope.launch {
            if (activeStatusId != null) taskRepository.getActiveTasksByIds(listOf(activeStatusId))
                .collect {
                    if (it.isNotEmpty()) with(it.first()) {
                        taskRepository.getByIds(listOf(id)).collect { l ->
                            if (l.isNotEmpty()) {
                                taskEdit = TaskEdit(
                                    task = l.first(),
                                    activeStatus = activeStatuses.first()
                                )
                                updateAssigningTask(true)
                            }
                        }
                    }
                }
            else if (taskId != null) getTask(taskId)
        }
    }

    private suspend fun getTask(id: Long) {
        taskRepository.getByIds(listOf(id)).firstOrNull()?.let {
            if (it.isNotEmpty()) with(it.first()) {
                taskEdit = TaskEdit(this).run {
                    copy(
                        activeStatus = activeStatus.copy(
                            period = period ?: TaskPeriod.DAY
                        )
                    )
                }
                updateAssigningTask(true)
            }
        }
    }

    fun updatePeriod(period: TaskPeriod) {
        this.period = period
    }

    fun updateAssigningTask(assigning: Boolean) {
        assigningTask = assigning
    }

    fun updateStatusPeriod(period: TaskPeriod) {
        taskEdit = taskEdit?.copy(
            selectedDueDate = if (period == TaskPeriod.DAY) taskEdit?.selectedDueDate?.copy(
                dayOfMonth = taskEdit?.selectedStartDate?.dayOfMonth
            ) else taskEdit?.selectedDueDate,
            activeStatus = taskEdit!!.activeStatus.copy(
                period = period
            )
        )
    }

    fun updateStatusReminder(set: Boolean) {
        taskEdit = taskEdit?.copy(
            activeStatus = taskEdit!!.activeStatus.copy(
                reminderSet = set
            )
        )
    }

    fun updateStatusDefault(set: Boolean) {
        taskEdit = taskEdit?.copy(
            activeStatus = taskEdit!!.activeStatus.copy(
                isDefault = set
            )
        )
    }

    fun updateStatusPriority(priority: TaskPriority) {
        taskEdit = taskEdit?.copy(
            activeStatus = taskEdit!!.activeStatus.copy(
                priority = priority
            )
        )
    }

    fun updateEditTask(task: Task) {
        viewModelScope.launch {
            getTask(task.id)
        }
    }

    // TODO handle notification
    suspend fun save() {
        taskEdit?.toTask()?.let {
            saveActiveTasksUseCase(listOf(it))
        }
    }

    internal fun updateStatusStartTime(time: Time) {
        taskEdit = taskEdit?.copy(
            selectedStartDate = taskEdit!!.selectedStartDate?.copy(time = time)
                ?: Date(time)
        )
    }

    internal fun updateStatusEndTime(time: Time) {
        taskEdit = taskEdit?.copy(
            selectedDueDate = taskEdit!!.selectedDueDate?.copy(time = time)
                ?: Date(time)
        )
    }

    internal fun updateStatusStartDate(date: Int) {
        taskEdit = taskEdit?.copy(
            selectedStartDate = taskEdit!!.selectedStartDate?.copy(dayOfMonth = date)
                ?: Date(dayOfMonth = date)
        )
    }

    internal fun updateStatusEndDate(date: Int) {
        taskEdit = taskEdit?.copy(
            selectedDueDate = taskEdit!!.selectedDueDate?.copy(dayOfMonth = date)
                ?: Date(dayOfMonth = date)
        )
    }
}