package com.nrr.taskmanagement

internal class SortState : CustomizeState<Customize.Sort>(
    selected = Customize.Sort.entries[0],
    expanded = false,
    options = Customize.Sort.entries
)