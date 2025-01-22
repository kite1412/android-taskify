package com.nrr.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.LanguageConfig
import com.nrr.model.NotificationOffset
import com.nrr.model.ThemeConfig
import com.nrr.model.TimeUnit
import com.nrr.settings.util.SettingsDictionary
import com.nrr.ui.DevicePreviews

@Composable
internal fun Content2Pane(
    selectedMenu: Menu,
    onMenuClick: (Menu) -> Unit,
    onBackClick: () -> Unit,
    theme: ThemeConfig,
    onThemeClick: (ThemeConfig) -> Unit,
    language: LanguageConfig,
    onLanguageClick: (LanguageConfig) -> Unit,
    pushNotification: Boolean,
    onPushNotificationClick: (Boolean) -> Unit,
    dayNotificationOffset: NotificationOffset,
    onDayTimeUnitClick: (TimeUnit) -> Unit,
    onDayOffsetChange: (Int) -> Unit,
    weekNotificationOffset: NotificationOffset,
    onWeekTimeUnitClick: (TimeUnit) -> Unit,
    onWeekOffsetChange: (Int) -> Unit,
    monthNotificationOffset: NotificationOffset,
    onMonthTimeUnitClick: (TimeUnit) -> Unit,
    onMonthOffsetChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(
                    max(
                        a = 240.dp,
                        b = (screenWidth / 3).dp
                    )
                )
                .background(MaterialTheme.colorScheme.onBackground)
                .padding(16.dp)
        ) {
            Header(
                onBackClick = onBackClick
            )
            Menus(
                selectedMenu = selectedMenu,
                onMenuClick = onMenuClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
        when (selectedMenu) {
            Menu.THEME -> ThemeConfig(
                theme = theme,
                onThemeClick = onThemeClick
            )
            Menu.LANGUAGES -> LanguagesConfig(
                language = language,
                onLanguageClick = onLanguageClick
            )
            Menu.NOTIFICATIONS -> NotificationsConfig(
                pushNotification = pushNotification,
                onPushNotificationClick = onPushNotificationClick,
                dayNotificationOffset = dayNotificationOffset,
                onDayTimeUnitClick = onDayTimeUnitClick,
                onDayOffsetChange = onDayOffsetChange,
                weekNotificationOffset = weekNotificationOffset,
                onWeekTimeUnitClick = onWeekTimeUnitClick,
                onWeekOffsetChange = onWeekOffsetChange,
                monthNotificationOffset = monthNotificationOffset,
                onMonthTimeUnitClick = onMonthTimeUnitClick,
                onMonthOffsetChange = onMonthOffsetChange
            )
        }
    }
}

@Composable
private fun Header(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                painter = painterResource(TaskifyIcon.back),
                contentDescription = "back"
            )
        }
        Text(
            text = stringResource(SettingsDictionary.settings),
            fontSize = TaskifyDefault.HEADER_FONT_SIZE.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun Menus(
    selectedMenu: Menu,
    onMenuClick: (Menu) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        MenuItem(
            menu = Menu.THEME,
            selected = selectedMenu == Menu.THEME,
            onClick = onMenuClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            ThemeMenu()
        }
        MenuItem(
            menu = Menu.LANGUAGES,
            selected = selectedMenu == Menu.LANGUAGES,
            onClick = onMenuClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            LanguagesMenu()
        }
        MenuItem(
            menu = Menu.NOTIFICATIONS,
            selected = selectedMenu == Menu.NOTIFICATIONS,
            onClick = onMenuClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            NotificationsMenu()
        }
    }
}

@Composable
private fun MenuItem(
    menu: Menu,
    selected: Boolean,
    onClick: (Menu) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onBackground
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White
            else LocalContentColor.current
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(16.dp)
            .clickable(
                indication = null,
                interactionSource = null,
                onClick = { onClick(menu) }
            )
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}

@DevicePreviews
@Composable
private fun Content2PanePreview() {
    var menu by remember { mutableStateOf(Menu.THEME) }

    TaskifyTheme {
        Content2Pane(
            selectedMenu = menu,
            onMenuClick = { menu = it },
            onBackClick = {},
            theme = ThemeConfig.SYSTEM_DEFAULT,
            onThemeClick = {},
            language = LanguageConfig.ENGLISH,
            onLanguageClick = {},
            pushNotification = true,
            onPushNotificationClick = {},
            dayNotificationOffset = NotificationOffset(1, TimeUnit.MINUTES),
            onDayTimeUnitClick = {},
            onDayOffsetChange = {},
            weekNotificationOffset = NotificationOffset(1, TimeUnit.MINUTES),
            onWeekTimeUnitClick = {},
            onWeekOffsetChange = {},
            monthNotificationOffset = NotificationOffset(1, TimeUnit.MINUTES),
            onMonthTimeUnitClick = {},
            onMonthOffsetChange = {}
        )
    }
}