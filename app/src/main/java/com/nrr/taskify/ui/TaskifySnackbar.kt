package com.nrr.taskify.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.theme.Blue
import com.nrr.designsystem.theme.TaskifyTheme

@Composable
fun TaskifySnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) = Snackbar(
    snackbarData = snackbarData,
    modifier = modifier.clip(RoundedCornerShape(16.dp)),
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = Color.White,
    dismissActionContentColor = Color.White,
    actionColor = Blue
)

@Preview
@Composable
private fun TaskifySnackbarPreview() {
    TaskifyTheme {
        TaskifySnackbar(
            snackbarData = object : SnackbarData {
                override val visuals: SnackbarVisuals
                    get() = object : SnackbarVisuals {
                        override val actionLabel: String
                            get() = "Action"
                        override val duration: SnackbarDuration
                            get() = SnackbarDuration.Short
                        override val message: String
                            get() = "A snackbar message"
                        override val withDismissAction: Boolean
                            get() = true
                    }

                override fun dismiss() {}

                override fun performAction() {}
            }
        )
    }
}