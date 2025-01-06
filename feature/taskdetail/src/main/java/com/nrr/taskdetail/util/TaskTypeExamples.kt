package com.nrr.taskdetail.util

import com.nrr.model.TaskType

internal fun TaskType.examplesId() = when (this) {
    TaskType.PERSONAL -> TaskDetailDictionary.personalExamples
    TaskType.LEARNING -> TaskDetailDictionary.learningExamples
    TaskType.HEALTH -> TaskDetailDictionary.healthExamples
    TaskType.WORK -> TaskDetailDictionary.workExamples
    TaskType.REFLECTION -> TaskDetailDictionary.reflectionExamples
    TaskType.SPECIAL -> TaskDetailDictionary.specialExamples
}