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

        val intFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
        else{
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingAlarmIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            alarmIntent,
            intFlags
        )

        Log.i("AlarmHelper", "Setting Alarm Interval to: $notificationFrequency minutes")

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

        val intFlags = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            0 or PendingIntent.FLAG_IMMUTABLE
        }
        else{
            PendingIntent.FLAG_UPDATE_CURRENT
        }

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
        Log.i("AlarmHelper", "Cancelling alarms")
    }

    fun checkAlarm(context: Context): Boolean {

        val alarmIntent = Intent(context, NotifierReceiver::class.java)
        alarmIntent.action = ACTION_BD_NOTIFICATION

        val intFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }

        return PendingIntent.getBroadcast(
            context, notificationId,
            alarmIntent,
            intFlags
        ) != null
    }
}