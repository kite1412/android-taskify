package com.nrr.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class SnackbarHostStateWrapper(
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val coroutineScope: CoroutineScope? = null
) {
    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        result: ((SnackbarResult) -> Unit)? = null
    ) = coroutineScope?.launch {
        val res = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration
        )
        result?.invoke(res)
    }
}

val LocalSnackbarHostState = compositionLocalOf {
    SnackbarHostStateWrapper()
}