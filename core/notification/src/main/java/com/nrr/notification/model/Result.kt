package com.nrr.notification.model

sealed interface Result {
    data class Fail(
        val reason: Reason
    ) : Result {
        enum class Reason {
            START_DATE_IN_PAST,
            EXACT_ALARM_NOT_PERMITTED
        }
    }
    data class Success(
        val warning: Warning?
    ) : Result {
        enum class Warning {
            END_REMINDER_SKIPPED,
            END_REMINDER_IN_PAST
        }
    }
}