package com.nrr.taskify.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.component.AppLogo
import com.nrr.designsystem.theme.TaskifyTheme
import kotlinx.coroutines.delay

@Composable
internal fun SplashScreen(
    onCompleted: (Long) -> Unit,
    modifier: Modifier = Modifier,
    showSplash: Boolean = true
) {
    var showLabel by rememberSaveable { mutableStateOf(false) }
    val dismissDelay = 500L

    LaunchedEffect(showSplash) {
        if (showSplash) {
            delay(500)
            showLabel = true
            delay(1500)
            onCompleted(dismissDelay)
        }
    }

    AnimatedVisibility(
        visible = showSplash,
        modifier = modifier.fillMaxSize(),
        exit = slideOutVertically(tween(dismissDelay.toInt())) { it * 2 }
    ) {
        Box {
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppLogo(modifier = Modifier.size(70.dp))
                AnimatedVisibility(
                    visible = showLabel,
                ) {
                    Text(
                        text = "Taskify",
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun SplashScreenPreview() {
    var showSplash by remember { mutableStateOf(true) }

    TaskifyTheme {
        Scaffold {
            SplashScreen(
                onCompleted = { showSplash = false },
                showSplash = showSplash
            )
        }
    }
}