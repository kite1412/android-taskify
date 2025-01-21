package com.nrr.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Gray
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.settings.util.SettingsDictionary

@Composable
internal fun Content(
    menu: Menu?,
    themeIndicator: String,
    languagesIndicator: String,
    notificationsIndicator: String,
    onMenuClick: (Menu) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Header(
            menu = menu,
            onBackClick = onBackClick
        )
        AnimatedContent(
            targetState = menu,
            transitionSpec = {
                fadeIn() + slideInHorizontally {
                    if (targetState == null) -it else it
                } togetherWith fadeOut() + slideOutHorizontally {
                    if (targetState == null) it else -it
                }
            }
        ) {
            if (it == null) Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MenuItem(
                    menu = Menu.THEME,
                    indicator = themeIndicator,
                    onMenuClick = onMenuClick
                ) {
                    ThemeMenu()
                }
                MenuItem(
                    menu = Menu.LANGUAGES,
                    indicator = languagesIndicator,
                    onMenuClick = onMenuClick
                ) {
                    LanguagesMenu()
                }
                MenuItem(
                    menu = Menu.NOTIFICATIONS,
                    indicator = notificationsIndicator,
                    onMenuClick = onMenuClick
                ) {
                    NotificationsMenu()
                }
            } else when (it) {
                Menu.THEME -> Unit
                Menu.LANGUAGES -> Unit
                Menu.NOTIFICATIONS -> Unit
            }
        }
    }
}

@Composable
private inline fun MenuItem(
    menu: Menu,
    indicator: String,
    crossinline onMenuClick: (Menu) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = null
            ) { onMenuClick(menu) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = indicator,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Gray
                )
            )
            Icon(
                painter = painterResource(TaskifyIcon.chevronDown),
                contentDescription = "more",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(-90f)
            )
        }
    }
}

@Composable
private fun Header(
    menu: Menu?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                painter = painterResource(TaskifyIcon.back),
                contentDescription = "back",
            )
        }
        Text(
            text = stringResource(
                when (menu) {
                    Menu.THEME -> SettingsDictionary.theme
                    Menu.LANGUAGES -> SettingsDictionary.languages
                    Menu.NOTIFICATIONS -> SettingsDictionary.notifications
                    null -> SettingsDictionary.settings
                }
            ),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = TaskifyDefault.HEADER_FONT_SIZE.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Preview
@Composable
private fun ContentPreview() {
    var curMenu by remember { mutableStateOf<Menu?>(null) }

    TaskifyTheme {
        Content(
            menu = curMenu,
            themeIndicator = "Theme",
            languagesIndicator = "Languages",
            notificationsIndicator = "Notifications",
            onMenuClick = { curMenu = it },
            onBackClick = { curMenu = null }
        )
    }
}