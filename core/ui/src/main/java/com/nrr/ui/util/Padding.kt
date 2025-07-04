package com.nrr.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.LocalScaffoldComponentSizes
import com.nrr.designsystem.ScaffoldComponent
import com.nrr.designsystem.util.TaskifyDefault

@Composable
fun rootContentBottomPadding(): Dp =
    (LocalScaffoldComponentSizes.current[ScaffoldComponent.BOTTOM_NAVIGATION_BAR]
        ?.height?.plus(16.dp) ?: 0.dp) + TaskifyDefault.CONTENT_PADDING.dp