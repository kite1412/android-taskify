package com.nrr.taskmanagement

import CustomizeState

internal class FilterState : CustomizeState<Customize.Filter>(
    selected = Customize.Filter.entries[0],
    expanded = false,
    options = Customize.Filter.entries
)