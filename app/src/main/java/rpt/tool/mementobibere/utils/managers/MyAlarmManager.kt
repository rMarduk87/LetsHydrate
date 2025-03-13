package rpt.tool.mementobibere.utils.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.newnotifications.BackupReceiver
import rpt.tool.mementobibere.utils.newnotifications.NewAlarmReceiver
import java.util.Calendar
import java.util.Locale


object MyAlarmManager {
    fun scheduleAutoRecurringAlarm(context: Context, updateTime2: Calendar, _id: Int) // for auto
    {
        val updateTime: Calendar = Calendar.getInstance(Locale.getDefault())
        updateTime.timeInMillis = updateTime2.timeInMillis

        if (updateTime.timeInMillis < System.currentTimeMillis()) {
            updateTime.add(Calendar.DATE, 1)
        }

        val intentAlarm = Intent(context, NewAlarmReceiver::class.java)

        val alarms = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val recurringDownload = PendingIntent.getBroadcast(
            context,
            _id,
            intentAlarm,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        d("MAINALARMID A :", _id.toString() + " @@@ " + updateTime.timeInMillis)

        alarms.setRepeating(
            AlarmManager.RTC_WAKEUP,
            updateTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            recurringDownload
        )
    }

    fun scheduleManualRecurringAlarm(
        context: Context,
        dayOfWeek: Int,
        hour: Int,
        minute: Int,
        _id: Int
    ) {
        val calendar: Calendar = Calendar.getInstance(Locale.getDefault())
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)

        // Check we aren't setting it in the past which would trigger it to fire instantly
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }


        val intentAlarm = Intent(context, NewAlarmReceiver::class.java)

        val alarms = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val recurringDownload = PendingIntent.getBroadcast(
            context,
            _id,
            intentAlarm,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        d("MAINALARMID M :", _id.toString() + " @@@ " + calendar.timeInMillis)

        alarms.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            recurringDownload
        )
    }

    fun cancelRecurringAlarm(context: Context, _id: Int) {
        d("MAINALARMID C :", "" + _id)

        val intentAlarm = Intent(context, NewAlarmReceiver::class.java)

        val alarms = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            _id,
            intentAlarm,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarms.cancel(pendingIntent)
    }

    fun scheduleAutoBackupAlarm(
        context: Context,
        updateTime2: Calendar,
        _id: Int,
        autoBackupType: Int
    ) // for auto backup
    {
        val updateTime = Calendar.getInstance(Locale.US)
        updateTime.timeInMillis = updateTime2.timeInMillis
        if (updateTime.timeInMillis < System.currentTimeMillis()) {
            updateTime.add(Calendar.DATE, 1)
        }


        val intentAlarm = Intent(context, BackupReceiver::class.java)

        val alarms = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val recurringDownload = PendingIntent.getBroadcast(
            context,
            _id,
            intentAlarm,
            PendingIntent.FLAG_IMMUTABLE
        )


        var interval = AlarmManager.INTERVAL_DAY

        if (autoBackupType == 1) interval = AlarmManager.INTERVAL_DAY * 7
        else if (autoBackupType == 2) interval = AlarmManager.INTERVAL_DAY * 30

        d(
            "MAINBACKUP A :",
            _id.toString() + " @@@ " + updateTime.timeInMillis + " @@@ " + interval
        )

        alarms.setRepeating(
            AlarmManager.RTC_WAKEUP, updateTime.timeInMillis,
            interval, recurringDownload
        )
    }
}