package com.nrr.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.nrr.designsystem.component.Checkbox
import com.nrr.designsystem.component.TaskifyCheckboxDefaults
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.model.ThemeConfig
import com.nrr.settings.util.SettingsDictionary

@Composable
internal fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val windowWidthClass =
        currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val menu = viewModel.currentMenu
    val userData by viewModel.userData.collectAsStateWithLifecycle()

    if (userData != null)
        if (windowWidthClass == WindowWidthSizeClass.COMPACT) Content(
            menu = menu,
            themeIndicator = "",
            languagesIndicator = "",
            notificationsIndicator = "",
            onMenuClick = viewModel::updateCurrentMenu,
            onBackClick = onBackClick,
            theme = viewModel.theme!!,
            onThemeClick = viewModel::updateTheme,
            modifier = modifier
        )
        else Content2Pane()
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
        name = stringResource(SettingsDictionary.theme)
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
internal inline fun SubMenu(
    name: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
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
        val whiteOrBlack = if (isSystemInDarkTheme()) Color.Black else Color.White

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