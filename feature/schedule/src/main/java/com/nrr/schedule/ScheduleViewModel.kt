package com.nrr.schedule

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TimeOffset
import com.nrr.model.TimeUnit
import com.nrr.model.toLocalDateTime
import com.nrr.schedule.navigation.ScheduleRoute
import com.nrr.schedule.util.TaskDuration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    internal val period = TaskPeriod.entries[savedStateHandle.toRoute<ScheduleRoute>().periodOrdinal]
    internal var timeOffset by mutableStateOf(TimeOffset(0, TimeUnit.MINUTES))
        private set
    internal var dailySchedule by mutableStateOf(true)
        private set
    internal var date by mutableStateOf(Clock.System.now().toLocalDateTime().date)
        private set
    internal val availableTasks = taskRepository.getTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    internal val taskDurations = mutableStateListOf<TaskDuration>()

    internal fun onTimeOffsetValueChange(new: Int) {
        timeOffset = timeOffset.copy(value = new)
    }

    internal fun onTimeOffsetTimeUnitChange(new: TimeUnit) {
        timeOffset = timeOffset.copy(timeUnit = new)
    }

    internal fun onDailyScheduleChange(new: Boolean) {
        dailySchedule = new
    }

    internal fun onDateChange(new: LocalDate) {
        date = new
    }

    internal fun onTaskSelect(task: Task) = taskDurations.add(
        TaskDuration(
            task = task,
            duration = 1.hours
        )
    ).also {
        Log.d("ScheduleViewModel", "added: $task")
    }

    internal fun onTaskRemove(task: TaskDuration) {
        taskDurations.forEach {
            if (task.uuid == it.uuid) {
                taskDurations.remove(it)
                return
            }
        }
    }
}