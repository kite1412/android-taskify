package com.nrr.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowWidthSizeClass
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.settings.util.SettingsDictionary

@Composable
internal fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val windowWidthClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    if (windowWidthClass == WindowWidthSizeClass.COMPACT)
        Content()
    else Content2Pane()
}

@Composable
private fun Content(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {

    }
}

@Composable
private fun Content2Pane(modifier: Modifier = Modifier) {

}

@Composable
private fun Menu(
    name: String,
    iconId: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = name,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun ThemeMenu(modifier: Modifier = Modifier) =
    Menu(
        name = stringResource(SettingsDictionary.theme),
        iconId = TaskifyIcon.palette,
        modifier = modifier
    )

@Composable
private fun LanguagesMenu(modifier: Modifier = Modifier) =
    Menu(
        name = stringResource(SettingsDictionary.languages),
        iconId = TaskifyIcon.language,
        modifier = modifier
    )

@Composable
private fun NotificationsMenu(modifier: Modifier = Modifier) =
    Menu(
        name = stringResource(SettingsDictionary.notifications),
        iconId = TaskifyIcon.bell,
        modifier = modifier
    )

@Preview
@Composable
private fun ContentPreview() {
    TaskifyTheme {

    }
}

@Preview
@Composable
private fun Content2PanePreview() {
    TaskifyTheme {

    }
}