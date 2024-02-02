package rpt.tool.mementobibere.utils.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.AlarmHelper
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager


class BootReceiver : BroadcastReceiver() {
    private val alarm = AlarmHelper()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action != null) {
            if (intent.action == "android.intent.action.BOOT_COMPLETED") {
                val notificationFrequency = SharedPreferencesManager.notificationFreq
                val notificationsNewMessage = true
                alarm.cancelAlarm(context!!)
                if (notificationsNewMessage) {
                    alarm.setAlarm(context, notificationFrequency.toLong())
                }
            }
        }
    }
}