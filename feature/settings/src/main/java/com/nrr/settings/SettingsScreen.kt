package com.nrr.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.nrr.designsystem.LocalDarkTheme
import com.nrr.designsystem.component.Checkbox
import com.nrr.designsystem.component.TaskifyCheckboxDefaults
import com.nrr.designsystem.component.Toggle
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Blue
import com.nrr.designsystem.theme.CharcoalClay30
import com.nrr.designsystem.theme.Gray
import com.nrr.designsystem.theme.Red
import com.nrr.model.LanguageConfig
import com.nrr.model.NotificationOffset
import com.nrr.model.PushNotificationConfig
import com.nrr.model.TaskPeriod
import com.nrr.model.ThemeConfig
import com.nrr.settings.util.SettingsDictionary
import com.nrr.settings.util.notificationOffsetConstraint
import com.nrr.settings.util.toStringLocalized
import com.nrr.ui.toStringLocalized
import kotlin.math.roundToInt

@Composable
internal fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val windowWidthClass =
        currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val routeMenu = viewModel.routeMenu
    val menu = routeMenu ?: viewModel.currentMenu
        ?: if (windowWidthClass != WindowWidthSizeClass.COMPACT) Menu.THEME else null
    val userData by viewModel.userData.collectAsStateWithLifecycle()
    val theme = userData?.themeConfig
    val language = userData?.languageConfig
    val pushNotification = userData?.pushNotification
    val dayNotificationOffset = userData?.dayNotificationOffset
    val weekNotificationOffset = userData?.weekNotificationOffset
    val monthNotificationOffset = userData?.monthNotificationOffset

    if (userData != null)
        if (windowWidthClass == WindowWidthSizeClass.COMPACT) {
            val backClick = {
                if (menu == null || routeMenu != null) onBackClick()
                else viewModel.updateCurrentMenu(null)
            }

            BackHandler(onBack = backClick)
            Content(
                menu = menu,
                themeIndicator = theme!!.toStringLocalized(),
                languagesIndicator = language!!.toString(),
                notificationsIndicator = pushNotification!!.toString(),
                onMenuClick = viewModel::updateCurrentMenu,
                onBackClick = backClick,
                theme = theme,
                onThemeClick = viewModel::updateTheme,
                language = language,
                onLanguageClick = viewModel::updateLanguage,
                pushNotification = pushNotification == PushNotificationConfig.PUSH_ALL,
                onPushNotificationClick = viewModel::updatePushNotification,
                dayNotificationOffset = dayNotificationOffset!!,
                onDayNotificationOffsetChange = viewModel::updateDayNotificationOffset,
                weekNotificationOffset = weekNotificationOffset!!,
                onWeekNotificationOffsetChange = viewModel::updateWeekNotificationOffset,
                monthNotificationOffset = monthNotificationOffset!!,
                onMonthNotificationOffsetChange = viewModel::updateMonthNotificationOffset,
                modifier = modifier
            )
        } else if (menu != null) Content2Pane(
            selectedMenu = menu,
            onMenuClick = viewModel::updateCurrentMenu,
            onBackClick = onBackClick,
            theme = theme!!,
            onThemeClick = viewModel::updateTheme,
            language = language!!,
            onLanguageClick = viewModel::updateLanguage,
            pushNotification = pushNotification == PushNotificationConfig.PUSH_ALL,
            onPushNotificationClick = viewModel::updatePushNotification,
            dayNotificationOffset = dayNotificationOffset!!,
            onDayNotificationOffsetChange = viewModel::updateDayNotificationOffset,
            weekNotificationOffset = weekNotificationOffset!!,
            onWeekNotificationOffsetChange = viewModel::updateWeekNotificationOffset,
            monthNotificationOffset = monthNotificationOffset!!,
            onMonthNotificationOffsetChange = viewModel::updateMonthNotificationOffset,
            modifier = modifier
        )
}

@Composable
internal fun ThemeMenu(modifier: Modifier = Modifier) =
    Menu(
        name = stringResource(SettingsDictionary.theme),
        iconId = TaskifyIcon.palette,
        modifier = modifier
    )

@Composable
internal fun LanguagesMenu(modifier: Modifier = Modifier) =
    Menu(
        name = stringResource(SettingsDictionary.languages),
        iconId = TaskifyIcon.language,
        modifier = modifier
    )

@Composable
internal fun NotificationsMenu(modifier: Modifier = Modifier) =
    Menu(
        name = stringResource(SettingsDictionary.notifications),
        iconId = TaskifyIcon.bell,
        modifier = modifier
    )

@Composable
private fun Menu(
    name: String,
    iconId: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = name,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
internal fun ThemeConfig(
    theme: ThemeConfig,
    onThemeClick: (ThemeConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    SubMenu(
        name = stringResource(SettingsDictionary.theme),
        modifier = modifier
    ) {
        ThemeConfig.entries.forEach { t ->
            Selectable(
                name = t.toString(),
                selected = t == theme,
                onClick = { onThemeClick(t) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
internal fun LanguagesConfig(
    language: LanguageConfig,
    onLanguageClick: (LanguageConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    SubMenu(
        name = stringResource(SettingsDictionary.language),
        modifier = modifier
    ) {
        LanguageConfig.entries.forEach { l ->
            Selectable(
                name = l.toString(),
                selected = l == language,
                onClick = { onLanguageClick(l) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
internal fun NotificationsConfig(
    pushNotification: Boolean,
    onPushNotificationClick: (Boolean) -> Unit,
    dayNotificationOffset: NotificationOffset,
    onDayNotificationOffsetChange: (NotificationOffset) -> Unit,
    weekNotificationOffset: NotificationOffset,
    onWeekNotificationOffsetChange: (NotificationOffset) -> Unit,
    monthNotificationOffset: NotificationOffset,
    onMonthNotificationOffsetChange: (NotificationOffset) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        SubMenu(
            name = stringResource(SettingsDictionary.notification)
        ) {
            Toggleable(
                name = stringResource(SettingsDictionary.showNotifications),
                checked = pushNotification,
                onCheckedChange = onPushNotificationClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
        SubMenu(
            name = stringResource(SettingsDictionary.reminderNotification),
            about = stringResource(SettingsDictionary.reminderNotificationAbout)
        ) {
            NotificationOffsetSetting(
                period = TaskPeriod.DAY,
                notificationOffset = dayNotificationOffset,
                onNotificationOffsetChange = onDayNotificationOffsetChange,
                enabled = pushNotification
            )
            NotificationOffsetSetting(
                period = TaskPeriod.WEEK,
                notificationOffset = weekNotificationOffset,
                onNotificationOffsetChange = onWeekNotificationOffsetChange,
                enabled = pushNotification
            )
            NotificationOffsetSetting(
                period = TaskPeriod.MONTH,
                notificationOffset = monthNotificationOffset,
                onNotificationOffsetChange = onMonthNotificationOffsetChange,
                enabled = pushNotification
            )
            NotificationOffsetWarning(
                pushNotification = pushNotification,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun NotificationOffsetWarning(
    pushNotification: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(SettingsDictionary.notificationOffsetWarning),
        style = MaterialTheme.typography.bodySmall.copy(
            color = if (pushNotification) Red else Gray,
            textAlign = TextAlign.Center
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationOffsetSetting(
    period: TaskPeriod,
    notificationOffset: NotificationOffset,
    onNotificationOffsetChange: (NotificationOffset) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val constraint = period.notificationOffsetConstraint()
    val selectableTimeUnits = constraint.selectableOffset.map { it.first }
    var sliderValue by remember {
        mutableFloatStateOf(notificationOffset.value.toFloat())
    }
    val selectedRange = constraint.selectableOffset.first {
        it.first == notificationOffset.timeUnit
    }
    val disabledColor = Color.Gray
    val contentColor = if (enabled) LocalContentColor.current
        else disabledColor

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Column(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = period.toStringLocalized(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Box {
                    var showTimeUnit by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = null,
                            enabled = enabled
                        ) {
                            showTimeUnit = !showTimeUnit
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val bodyMedium = MaterialTheme.typography.bodyMedium

                        Text(
                            text = "${sliderValue.roundToInt()} " + notificationOffset.timeUnit.toStringLocalized(),
                            style = bodyMedium
                        )
                        Icon(
                            painter = painterResource(TaskifyIcon.chevronDown),
                            contentDescription = "more",
                            modifier = Modifier.size(bodyMedium.fontSize.value.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showTimeUnit,
                        onDismissRequest = { showTimeUnit = false },
                        modifier = Modifier.background(CharcoalClay30)
                    ) {
                        selectableTimeUnits.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = it.toStringLocalized(),
                                        color = if (notificationOffset.timeUnit == it)
                                            Blue else Color.White
                                    )
                                },
                                onClick = {
                                    if (notificationOffset.timeUnit != it) {
                                        showTimeUnit = false
                                        sliderValue = 1f
                                        onNotificationOffsetChange(
                                            NotificationOffset(1, it)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
            val activeColor = MaterialTheme.colorScheme.primary
            val inactiveColor = activeColor.copy(alpha = 0.5f)
            val interactionSource = remember { MutableInteractionSource() }

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = with(selectedRange) {
                    second.first.toFloat()..second.last.toFloat()
                },
                onValueChangeFinished = {
                    onNotificationOffsetChange(
                        notificationOffset.copy(value = sliderValue.roundToInt())
                    )
                },
                steps = with(selectedRange.second) {
                    last - start - 1
                },
                colors = SliderDefaults.colors(
                    inactiveTrackColor = inactiveColor,
                    inactiveTickColor = Color.Transparent,
                    activeTrackColor = activeColor,
                    activeTickColor = Color.Transparent,
                    disabledInactiveTrackColor = disabledColor,
                    disabledActiveTrackColor = disabledColor,
                    disabledActiveTickColor = disabledColor,
                    disabledInactiveTickColor = disabledColor,
                    thumbColor = activeColor,
                    disabledThumbColor = disabledColor
                ),
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
                        thumbSize = DpSize(
                            width = 2.dp,
                            height = 24.dp
                        )
                    )
                },
                enabled = enabled
            )
        }
    }
}

@Composable
internal inline fun SubMenu(
    name: String,
    modifier: Modifier = Modifier,
    about: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val bodyLarge = MaterialTheme.typography.bodyLarge

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    style = bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                about?.let {
                    var show by remember { mutableStateOf(false) }
                    val density = LocalDensity.current

                    Box {
                        Icon(
                            painter = painterResource(TaskifyIcon.info),
                            contentDescription = "about",
                            modifier = Modifier
                                .size(bodyLarge.fontSize.value.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = null
                                ) { show = !show }
                        )
                        if (show) Popup(
                            onDismissRequest = { show = false },
                            offset = IntOffset(
                                x = 0,
                                y = with(density) {
                                    bodyLarge.fontSize.roundToPx()
                                }
                            )
                        ) {
                            val shape = RoundedCornerShape(16.dp)

                            Box(
                                modifier = Modifier
                                    .widthIn(
                                        min = 0.dp,
                                        max = 240.dp
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = LocalContentColor.current,
                                        shape = shape
                                    )
                                    .background(
                                        color = MaterialTheme.colorScheme.background,
                                        shape = shape
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(LocalContentColor.current)
                    .clip(CircleShape)
            )
        }
        Column(
            modifier = Modifier.padding(start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun Selectable(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = null,
            onClick = onClick
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val contentColor = LocalContentColor.current
        val isDarkTheme = LocalDarkTheme.current
        val whiteOrBlack = if (isDarkTheme) Color.Black else Color.White

        Text(name)
        Checkbox(
            checked = selected,
            onCheckedChange = { onClick() },
            colors = TaskifyCheckboxDefaults.colors(
                checkedColor = contentColor,
                uncheckedColor = contentColor,
                checkmarkColor = whiteOrBlack
            )
        )
    }
}

@Composable
private fun Toggleable(
    name: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = null,
            onClick = { onCheckedChange(!checked) }
        ),
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