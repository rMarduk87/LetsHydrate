package rpt.tool.letshydrate.utils.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import rpt.tool.letshydrate.utils.AppUtils
import rpt.tool.letshydrate.utils.helpers.AlarmHelper
import rpt.tool.letshydrate.utils.managers.SharedPreferencesManager


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