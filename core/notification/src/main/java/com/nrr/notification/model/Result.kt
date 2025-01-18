package com.nrr.notification.model

sealed interface Result {
    data class Fail(
        val reason: Reason
    ) : Result {
        enum class Reason {
            START_DATE_IN_PAST
        }
    }
    data object Success : Result
}