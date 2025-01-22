package com.nrr.designsystem

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.DpSize

typealias ScaffoldComponentSizes = Map<ScaffoldComponent, DpSize>

// provides sizes of rendered scaffold components
val LocalScaffoldComponentSizes = compositionLocalOf<ScaffoldComponentSizes> {
    mapOf()
}

enum class ScaffoldComponent {
    TOP_BAR,
    BOTTOM_NAVIGATION_BAR,
    DRAWER_NAVIGATION_BAR,
    NAVIGATION_RAIL
}