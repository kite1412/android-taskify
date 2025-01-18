package com.nrr.notification.typeadapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.nrr.notification.model.ReminderType
import com.nrr.notification.model.TaskFiltered
import com.nrr.notification.model.TaskWithReminder
import kotlinx.datetime.Instant
import java.lang.reflect.Type

class TaskWithReminderAdapter : 
    JsonSerializer<TaskWithReminder>,
    JsonDeserializer<TaskWithReminder> {
    override fun serialize(
        src: TaskWithReminder?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val task = src?.task
        val taskJson = JsonObject().apply {
            addProperty("id", task?.id)
            addProperty("title", task?.title)
            addProperty("startDate", task?.startDate?.toEpochMilliseconds())
            addProperty("dueDate", task?.dueDate?.toEpochMilliseconds())
        }

        return JsonObject().apply {
            add("task", taskJson)
            addProperty("reminderType", src?.reminderType?.name)
        }
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): TaskWithReminder =
        with(json?.asJsonObject) {
            TaskWithReminder(
                task = with(this?.get("task")?.asJsonObject) {
                    TaskFiltered(
                        id = this?.get("id")?.asLong ?: 0,
                        title = this?.get("title")?.asString ?: "",
                        startDate = Instant.fromEpochMilliseconds(
                            this?.get("startDate")?.asLong ?: 0L
                        ),
                        dueDate = this?.get("dueDate")?.asLong?.let {
                            Instant.fromEpochMilliseconds(it)
                        }
                    )
                },
                reminderType = this?.get("reminderType")?.asString?.let {
                    ReminderType.valueOf(it)
                } ?: ReminderType.START
            )
        }
}