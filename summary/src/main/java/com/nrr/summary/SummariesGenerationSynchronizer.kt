package com.nrr.summary

import com.nrr.data.repository.SummaryRepository
import com.nrr.data.repository.TaskRepository
import com.nrr.data.repository.UserDataRepository
import com.nrr.model.TaskPeriod
import com.nrr.model.getEndDate
import com.nrr.model.getStartDate
import com.nrr.model.toLocalDateTime
import com.nrr.notification.ScheduledTaskNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject
import kotlin.math.max
import kotlin.time.Duration.Companion.days

class SummariesGenerationSynchronizer @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val taskRepository: TaskRepository,
    private val summaryRepository: SummaryRepository,
    private val scheduledTaskNotifier: ScheduledTaskNotifier
) {
    fun synchronize() {
        CoroutineScope(Dispatchers.Default).launch {
            val report = userDataRepository.userData.first().summariesGenerationReport
            val lastGenerationDate = report.lastGenerationDate
            val today = Clock.System.now()
            val todayStart = today.getStartDate(TaskPeriod.DAY)
            val todayEnd = today.getEndDate(TaskPeriod.DAY)

            if (lastGenerationDate in todayStart..todayEnd) return@launch

            userDataRepository.setSummariesGenerationReport(
                report = report.copy(
                    lastGenerationDate = today
                )
            )

            generatePastSummaries(DayNextPeriodResolver)
            generatePastSummaries(WeekNextPeriodResolver)
            generatePastSummaries(MonthNextPeriodResolver)
        }
    }

    private suspend fun generatePastSummaries(
        nextPeriodResolver: NextPeriodResolver
    ) {
        val period = nextPeriodResolver.period
        val tasks = taskRepository.getActiveTasksByPeriod(period)
            .firstOrNull() ?: return

        if (tasks.isEmpty()) return

        val grouped = tasks.groupBy {
            it.activeStatuses.first().startDate.getStartDate(period)
        }

        grouped.forEach { (startDate, tasks) ->
            summaryRepository.createSummary(
                period = period,
                startDate = startDate
            )
            val (defaults, nonDefaults) = tasks.partition {
                it.activeStatuses.first().isDefault
            }

            if (nonDefaults.isNotEmpty()) taskRepository.deleteActiveTasks(nonDefaults)

            if (defaults.isNotEmpty()) {
                val now = Clock.System.now()
                val adjustedTasks = defaults.map {
                    it.copy(
                        activeStatuses = it.activeStatuses.map { s ->
                            with(nextPeriodResolver) {
                                s.copy(
                                    startDate = s.startDate.getNextPeriod(now),
                                    dueDate = s.dueDate?.getNextPeriod(now),
                                    completedAt = null,
                                    isSet = true
                                )
                            }
                        }
                    )
                }

                taskRepository.saveActiveTasks(adjustedTasks)
                scheduledTaskNotifier.scheduleReminders(adjustedTasks, period)
            }
        }
    }

    private interface NextPeriodResolver {
        val period: TaskPeriod

        fun Instant.getNextPeriod(now: Instant): Instant
    }

    private object DayNextPeriodResolver : NextPeriodResolver {
        override val period = TaskPeriod.DAY

        override fun Instant.getNextPeriod(now: Instant): Instant {
            val date = toLocalDateTime()
            val nowDate = now.toLocalDateTime()

            return LocalDateTime(
                time = date.time,
                date = nowDate.date
            ).toInstant(TimeZone.currentSystemDefault())
        }
    }

    private object WeekNextPeriodResolver : NextPeriodResolver {
        override val period: TaskPeriod = TaskPeriod.WEEK

        override fun Instant.getNextPeriod(now: Instant): Instant {
            val date = toLocalDateTime()
            val nowDate = now.toLocalDateTime()
            val adjusted = (now + (date.dayOfWeek.value - nowDate.dayOfWeek.value).days)
                .toLocalDateTime()

            return adjusted.toInstant(TimeZone.currentSystemDefault())
        }
    }

    private object MonthNextPeriodResolver : NextPeriodResolver {
        override val period: TaskPeriod = TaskPeriod.MONTH

        override fun Instant.getNextPeriod(now: Instant): Instant {
            val date = toLocalDateTime()
            val nowDate = now.toLocalDateTime()

            return LocalDateTime(
                time = date.time,
                date = LocalDate(
                    year = nowDate.year,
                    monthNumber = nowDate.monthNumber,
                    dayOfMonth = max(
                        a = date.dayOfMonth,
                        b = nowDate.dayOfMonth
                    )
                )
            ).toInstant(TimeZone.currentSystemDefault())
        }
    }
}