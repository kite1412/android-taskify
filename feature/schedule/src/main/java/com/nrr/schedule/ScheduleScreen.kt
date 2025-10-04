package com.nrr.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrr.designsystem.component.RoundRectButton
import com.nrr.designsystem.component.Toggle
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.model.TaskPeriod
import com.nrr.model.TimeOffset
import com.nrr.model.TimeUnit
import com.nrr.model.toLocalDateTime
import com.nrr.schedule.util.ScheduleDictionary
import com.nrr.ui.Header
import com.nrr.ui.layout.MainLayout
import com.nrr.ui.picker.date.DatePicker
import com.nrr.ui.toStringLocalized
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Composable
internal fun ScheduleScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    Content(
        period = viewModel.period,
        timeOffset = viewModel.timeOffset,
        date = viewModel.date,
        onValueChange = viewModel::onTimeOffsetValueChange,
        onTimeUnitChange = viewModel::onTimeOffsetTimeUnitChange,
        dailySchedule = viewModel.dailySchedule,
        onDailyScheduleChange = viewModel::onDailyScheduleChange,
        onDateChange = viewModel::onDateChange,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    period: TaskPeriod,
    timeOffset: TimeOffset,
    dailySchedule: Boolean,
    date: LocalDate,
    onValueChange: (Int) -> Unit,
    onTimeUnitChange: (TimeUnit) -> Unit,
    onDailyScheduleChange: (Boolean) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pickDate by rememberSaveable { mutableStateOf(false) }

    MainLayout(modifier = modifier) {
        Header(
            title = stringResource(
                when (period) {
                    TaskPeriod.DAY -> ScheduleDictionary.dailySchedule
                    TaskPeriod.WEEK -> ScheduleDictionary.weeklySchedule
                    TaskPeriod.MONTH -> ScheduleDictionary.monthlySchedule
                }
            ),
            onBackClick = onBackClick
        )
        Date(
            selectedDate = date,
            onClick = { pickDate = true },
            modifier = Modifier.align(Alignment.End)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OffsetBetweenTasks(
                timeOffset = timeOffset,
                onValueChange = onValueChange,
                onTimeUnitChange = onTimeUnitChange
            )
            ToggleSetting(
                name = stringResource(ScheduleDictionary.dailyScheduleSetting),
                checked = dailySchedule,
                onCheckedChange = onDailyScheduleChange
            )
        }
    }

    if (pickDate) DatePicker(
        onDismiss = { pickDate = false },
        onConfirm = {
            onDateChange(
                Instant.fromEpochMilliseconds(it).toLocalDateTime().date
            )
            pickDate = false
        },
        confirmText = stringResource(ScheduleDictionary.choose),
        cancelText = stringResource(ScheduleDictionary.cancel),
        title = stringResource(ScheduleDictionary.chooseDate)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OffsetBetweenTasks(
    timeOffset: TimeOffset,
    onValueChange: (Int) -> Unit,
    onTimeUnitChange: (TimeUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(ScheduleDictionary.gapBetweenTasks))
            Box {
                var showOptions by remember { mutableStateOf(false) }

                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        modifier = Modifier.clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            showOptions = !showOptions
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = timeOffset.value.toString() + " ${
                                stringResource(
                                    id = when (timeOffset.timeUnit) {
                                        TimeUnit.MINUTES -> ScheduleDictionary.minute
                                        else -> ScheduleDictionary.hour
                                    }
                                )
                            }",
                            style = textStyle
                        )
                        Icon(
                            painter = painterResource(TaskifyIcon.chevronDown),
                            contentDescription = null,
                            modifier = Modifier.size(textStyle.fontSize.value.dp)
                        )
                    }
                }
                DropdownMenu(
                    expanded = showOptions,
                    onDismissRequest = { showOptions = false }
                ) {
                    val onTimeUnitChange = { unit: TimeUnit ->
                        onTimeUnitChange(unit)
                        onValueChange(0)
                        showOptions = false
                    }

                    DropdownMenuItem(
                        text = {
                            Text(stringResource(ScheduleDictionary.minute))
                        },
                        onClick = { onTimeUnitChange(TimeUnit.MINUTES) }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(ScheduleDictionary.hour))
                        },
                        onClick = { onTimeUnitChange(TimeUnit.HOURS) }
                    )
                }
            }
        }
        val interactionSource = remember { MutableInteractionSource() }

        Slider(
            value = timeOffset.value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            steps = (if (timeOffset.timeUnit == TimeUnit.MINUTES) 60 else 12) - 1,
            valueRange = 0f..(if (timeOffset.timeUnit == TimeUnit.MINUTES) 60f else 12f),
            colors = SliderDefaults.colors(
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    thumbSize = with (24.dp) {
                        DpSize(
                            width = this / 5,
                            height = this
                        )
                    }
                )
            }
        )
    }
}

@Composable
private fun ToggleSetting(
    name: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name)
        Toggle(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun Date(
    selectedDate: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoundRectButton(
        onClick = onClick,
        action = selectedDate.toStringLocalized(),
        modifier = modifier.fillMaxWidth(),
        iconId = TaskifyIcon.calendar,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun TaskPicker(modifier: Modifier = Modifier) {

}