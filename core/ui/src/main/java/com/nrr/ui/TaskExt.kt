package com.nrr.ui

import com.nrr.model.Task

fun Task.color() = taskType.color()

fun Task.iconId() = taskType.iconId()