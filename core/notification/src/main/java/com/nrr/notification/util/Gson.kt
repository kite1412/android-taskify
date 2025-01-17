package com.nrr.notification.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nrr.notification.model.TaskWithReminder
import com.nrr.notification.typeadapter.TaskWithReminderAdapter


internal fun <T> gson(block: Gson.() -> T) = block(
    GsonBuilder()
        .registerTypeAdapter(TaskWithReminder::class.java, TaskWithReminderAdapter())
        .create()
)