package com.nrr.taskmanagement

import androidx.annotation.StringRes
import com.nrr.taskmanagement.util.TaskManagementDictionary

internal enum class ConfirmationType(
    @StringRes val title: Int,
    @StringRes val confirmText: Int,
    @StringRes val cancelText: Int,
    @StringRes val confirmationDesc: Int,
    var totalAffected: Int
) {
    REMOVE_ALL(
        title = TaskManagementDictionary.removeAll,
        confirmText = TaskManagementDictionary.remove,
        cancelText = TaskManagementDictionary.cancel,
        confirmationDesc = TaskManagementDictionary.removeAllConfirmation,
        totalAffected = 0
    ),
    DELETE_ALL(
        title = TaskManagementDictionary.deleteAll,
        confirmText = TaskManagementDictionary.delete,
        cancelText = TaskManagementDictionary.cancel,
        confirmationDesc = TaskManagementDictionary.deleteAllConfirmation,
        totalAffected = 0
    );

    fun updateTotalAffected(value: Int) {
        totalAffected = value
    }
}