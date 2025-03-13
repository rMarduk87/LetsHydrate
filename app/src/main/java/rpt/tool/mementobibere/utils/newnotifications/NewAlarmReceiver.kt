package rpt.tool.mementobibere.utils.newnotifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import rpt.tool.mementobibere.utils.log.d
import java.util.Calendar
import java.util.Locale


class NewAlarmReceiver : BroadcastReceiver() {
    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context, intent: Intent) {
        d(DEBUG_TAG, "Recurring alarm; requesting download service.")

        WakeLocker.acquire(context)
        WakeLocker.release()

        val action = "" + intent.action

        if (action.equals("SNOOZE_ACTION", ignoreCase = true)) {

            val snoozeIntent = Intent(context, NewAlarmReceiver::class.java)
            val snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent,
                PendingIntent.FLAG_IMMUTABLE)
            val alarms = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarms.setExact(
                AlarmManager.RTC_WAKEUP,
                Calendar.getInstance(Locale.getDefault()).timeInMillis + 2 * 60000,
                snoozePendingIntent
            )

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(99)
        } else if (action == "android.intent.action.BOOT_COMPLETED") {

            val alarmHelper = NewAlarmHelper(context)
            alarmHelper.createAlarm()
        } else {

            val notificationHelper = NewNotificationHelper(context)
            notificationHelper.createNotification()
        }

    }

    companion object {
        private const val DEBUG_TAG = "AlarmReceiver"
    }
}