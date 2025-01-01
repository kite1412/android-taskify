package com.nrr.taskmanagement

import CustomizeState

internal class SortState : CustomizeState<Customize.Sort>(
    selected = Customize.Sort.entries[0],
    expanded = false,
    options = Customize.Sort.entries
)