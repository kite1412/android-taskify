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
import com.nrr.data.repository.UserDataRepository
import com.nrr.domain.RemoveActiveTasksUseCase
import com.nrr.domain.SaveActiveTasksUseCase
import com.nrr.model.TimeOffset
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskPriority
import com.nrr.model.toLocalDateTime
import com.nrr.planarrangement.navigation.PlanArrangementRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class PlanArrangementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    userDataRepository: UserDataRepository,
    private val taskRepository: TaskRepository,
    private val saveActiveTasksUseCase: SaveActiveTasksUseCase,
    private val removeActiveTasksUseCase: RemoveActiveTasksUseCase
) : ViewModel() {
    // navigation from non-null ActiveStatus to be used in TaskEdit typically for
    // editing existing ActiveStatus
    // if not null navigate back to calling route instead of making TaskEdit null
    val activeStatusId = savedStateHandle.toRoute<PlanArrangementRoute>().activeStatusId

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

    var deleteWarning by mutableStateOf(false)
        private set

    internal var taskEdit by mutableStateOf<TaskEdit?>(null)
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    internal val notificationOffset = snapshotFlow { taskEdit }
        .map {
            it?.activeStatus?.period
        }
        .flatMapLatest { p ->
            p?.let {
                userDataRepository.userData.map {
                    when (p) {
                        TaskPeriod.DAY -> it.dayNotificationOffset
                        TaskPeriod.WEEK -> it.weekNotificationOffset
                        TaskPeriod.MONTH -> it.monthNotificationOffset
                    }
                }
            } ?: flowOf(TimeOffset.Default)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TimeOffset.Default
        )

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
                && (it.selectedDueDate == null || it.selectedStartDate < it.selectedDueDate)
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
        val curDate = Clock.System.now().toLocalDateTime()

        taskEdit = taskEdit?.copy(
            selectedDueDate = if (period == TaskPeriod.DAY) taskEdit?.selectedDueDate?.copy(
                dayOfMonth = taskEdit?.selectedStartDate?.dayOfMonth,
                month = curDate.monthNumber
            ) else taskEdit?.selectedDueDate,
            activeStatus = taskEdit!!.activeStatus.copy(
                period = period
            ),
            selectedStartDate = if (period == TaskPeriod.DAY) taskEdit?.selectedStartDate?.copy(
                dayOfMonth = curDate.dayOfMonth,
                month = curDate.monthNumber
            ) else taskEdit?.selectedStartDate
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

    fun updateDeleteWarning(value: Boolean) {
        deleteWarning = value
    }

    suspend fun save() {
        taskEdit?.toTask()?.let { t ->
            saveActiveTasksUseCase(listOf(t))
        }
    }

    suspend fun deleteActiveTask() {
        taskEdit?.toTask()?.let {
            removeActiveTasksUseCase(listOf(it))
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

    internal fun updateStatusStartDate(day: Int, month: Int) {
        taskEdit = taskEdit?.copy(
            selectedStartDate = taskEdit!!.selectedStartDate?.copy(dayOfMonth = day, month = month)
                ?: Date(dayOfMonth = day, month = month)
        )
    }

    internal fun updateStatusEndDate(date: Int, month: Int) {
        taskEdit = taskEdit?.copy(
            selectedDueDate = taskEdit!!.selectedDueDate?.copy(dayOfMonth = date, month = month)
                ?: Date(dayOfMonth = date, month = month)
        )
    }
}