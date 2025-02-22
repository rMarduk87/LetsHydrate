package rpt.tool.mementobibere.utils.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import rpt.tool.mementobibere.utils.AppUtils.Companion.notificationId
import rpt.tool.mementobibere.utils.log.i
import java.util.concurrent.TimeUnit
import rpt.tool.mementobibere.utils.notifications.NotifierReceiver
import rpt.tool.mementobibere.utils.notifications.BootReceiver

class AlarmHelper {
    private var alarmManager: AlarmManager? = null

    private val ACTION_BD_NOTIFICATION = "rpt.tool.mementobibere.NOTIFICATION"

    fun setAlarm(context: Context, notificationFrequency: Long) {
        val notificationFrequencyMs = TimeUnit.MINUTES.toMillis(notificationFrequency)

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, NotifierReceiver::class.java)
        alarmIntent.action = ACTION_BD_NOTIFICATION

        val intFlags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val pendingAlarmIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            alarmIntent,
            intFlags
        )

        i("AlarmHelper", "Setting Alarm Interval to: $notificationFrequency minutes")

        alarmManager!!.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            notificationFrequencyMs,
            pendingAlarmIntent
        )

        /* Restart if rebooted */
        val receiver = ComponentName(context, BootReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun cancelAlarm(context: Context) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, NotifierReceiver::class.java)
        alarmIntent.action = ACTION_BD_NOTIFICATION

        val intFlags =
            0 or PendingIntent.FLAG_IMMUTABLE

        val pendingAlarmIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            alarmIntent,
            intFlags
        )
        alarmManager!!.cancel(pendingAlarmIntent)

        /* Alarm won't start again if device is rebooted */
        val receiver = ComponentName(context, BootReceiver::class.java)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        i("AlarmHelper", "Cancelling alarms")
    }

    fun checkAlarm(context: Context): Boolean {

        val alarmIntent = Intent(context, NotifierReceiver::class.java)
        alarmIntent.action = ACTION_BD_NOTIFICATION

        val intFlags =
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getBroadcast(
            context, notificationId,
            alarmIntent,
            intFlags
        ) != null
    }
}