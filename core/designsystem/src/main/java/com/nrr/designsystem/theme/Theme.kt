package com.nrr.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = VelvetRose,
    secondary = SpinachWhite,
    tertiary = Blue,
    background = SpinachWhite,
    onBackground = CilantroCream,
    surface = CilantroCream,
    surfaceContainer = CilantroCream,
    surfaceContainerLow = CilantroCream
)

private val DarkColorScheme = darkColorScheme(
    primary = VelvetRose,
    secondary = CharcoalClay,
    tertiary = Blue,
    background = CharcoalClay,
    onBackground = CharcoalClay30,
    surface = CharcoalClay30,
    surfaceContainer = CharcoalClay30,
    surfaceContainerLow = CharcoalClay30
)

@Composable
fun TaskifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalContentColor provides if (darkTheme) Color.White else Color.Black
        ) {
            content()
        }
    }
}