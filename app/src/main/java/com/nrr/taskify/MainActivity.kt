package com.nrr.taskify

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.ThemeConfig
import com.nrr.taskify.ui.TaskifyApp
import com.nrr.taskify.ui.TaskifyViewModel
import com.nrr.ui.LocalExactAlarmState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: TaskifyViewModel by viewModels()

    private var exactAlarmEnabled by mutableStateOf(false)

    private var exactAlarmStateReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { false }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            exactAlarmEnabled = (getSystemService(Context.ALARM_SERVICE) as AlarmManager)
                .canScheduleExactAlarms()
            if (!exactAlarmEnabled) {
                exactAlarmStateReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        exactAlarmEnabled = true
                    }
                }
            }
        }
        enableEdgeToEdge()
        setContent {
            val userData by viewModel.userData.collectAsStateWithLifecycle()

            TaskifyTheme(
                darkTheme = when (userData?.themeConfig) {
                    ThemeConfig.LIGHT -> false
                    ThemeConfig.DARK -> true
                    else -> isSystemInDarkTheme()
                }
            ){
                CompositionLocalProvider(
                    LocalExactAlarmState provides exactAlarmEnabled
                ) {
                    TaskifyApp(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            exactAlarmStateReceiver?.let {
                registerReceiver(
                    it,
                    IntentFilter(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED)
                )
            }
        }
    }
}