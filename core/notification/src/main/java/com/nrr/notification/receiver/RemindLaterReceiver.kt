package com.nrr.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint

const val REMIND_LATER_ACTION = "com.nrr.notification.REMIND_LATER"

@AndroidEntryPoint
class RemindLaterReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

    }

    companion object {
        const val REMINDER_TYPE_ORDINAL_KEY = "reminderTypeOrdinal"
    }
}