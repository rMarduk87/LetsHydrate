package rpt.tool.mementobibere.utils.newnotifications


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.SelectBottleActivity
import rpt.tool.mementobibere.SelectSnoozeActivity
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.R

internal class NewNotificationHelper(private var mContext: Context) {

    var sqliteHelper:SqliteHelper? = null

    init {
        sqliteHelper = SqliteHelper(mContext)
        if (AppUtils.notification_ringtone == null) AppUtils.notification_ringtone =
            RingtoneManager.getRingtone(
                mContext,
                sound
            )
    }

    fun createNotification() {
        d("createNotification", "" + SharedPreferencesManager.reminderOpt)
        d("createNotification V", "" + SharedPreferencesManager.reminderVibrate)



        if (SharedPreferencesManager.reminderOpt == 1) return

        if (reachedDailyGoal() && SharedPreferencesManager.disableNotificationAtGoal) return


        val intent = Intent(mContext, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val resultPendingIntent =
            PendingIntent.getActivity(mContext, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val snoozeIntent = Intent(mContext, SelectSnoozeActivity::class.java)
        snoozeIntent.setAction("SNOOZE_ACTION")
        snoozeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val snoozePendingIntent =
            PendingIntent.getActivity(mContext, 99, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val addWaterIntent = Intent(mContext, SelectBottleActivity::class.java)
        addWaterIntent.setAction("ADD_WATER_ACTION")
        addWaterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val addWaterPendingIntent = PendingIntent.getActivity(
            mContext,
            99,
            addWaterIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val mBuilder = NotificationCompat.Builder(mContext)
        mBuilder.setSmallIcon(R.drawable.ic_small_icon)
        mBuilder.setLargeIcon(
            BitmapFactory.decodeResource(
                mContext.resources,
                R.mipmap.ic_launcher
            )
        )
        mBuilder.setContentTitle(mContext.resources.getString(R.string.str_drink_water))
            .setContentText("" + get_today_report())
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))

        if (SharedPreferencesManager.reminderOpt == 0 && !SharedPreferencesManager.reminderVibrate) {
            mBuilder.setDefaults(Notification.DEFAULT_ALL)
        } else if (SharedPreferencesManager.reminderOpt == 1 && SharedPreferencesManager.reminderVibrate) {
            mBuilder.setDefaults(Notification.DEFAULT_SOUND)
        } else if (SharedPreferencesManager.reminderOpt == 2 && !SharedPreferencesManager.reminderVibrate) {
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
        }

        mBuilder.addAction(
            R.drawable.ic_plus,
            mContext.resources.getString(R.string.str_add_water),
            addWaterPendingIntent
        )
        mBuilder.addAction(
            R.drawable.ic_notification,
            mContext.resources.getString(R.string.str_snooze),
            snoozePendingIntent
        )
        
        val mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



        mNotificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID)
        mNotificationManager.deleteNotificationChannel(NOTIFICATION_VIBRATE_CHANNEL_ID)

        if (SharedPreferencesManager.reminderOpt == 0) {
            if (!SharedPreferencesManager.reminderVibrate) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel =
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.setSound(null, null)
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern =
                    longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)


                checkNotNull(mNotificationManager)
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)


                mNotificationManager.createNotificationChannel(notificationChannel)
            } else {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_VIBRATE_CHANNEL_ID,
                    "Vibrate Reminder",
                    importance
                )
                notificationChannel.enableLights(true)
                notificationChannel.setSound(null, null)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(false)
                notificationChannel.vibrationPattern = longArrayOf(0)


                checkNotNull(mNotificationManager)
                mBuilder.setChannelId(NOTIFICATION_VIBRATE_CHANNEL_ID)

                mNotificationManager.createNotificationChannel(notificationChannel)
            }

            try {
                if (!AppUtils.notification_ringtone!!.isPlaying)
                    AppUtils.notification_ringtone!!.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            if (!SharedPreferencesManager.reminderVibrate) {
                val channel_none = NotificationChannel(
                    NOTIFICATION_SILENT_CHANNEL_ID,
                    "Silent Reminder",
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel_none.setSound(null, null)
                channel_none.enableVibration(true)
                channel_none.vibrationPattern =
                    longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

                checkNotNull(mNotificationManager)
                mBuilder.setChannelId(NOTIFICATION_SILENT_CHANNEL_ID)
                mNotificationManager.createNotificationChannel(channel_none)
            } else {
                val channel_none = NotificationChannel(
                    NOTIFICATION_SILENT_VIBRATE_CHANNEL_ID,
                    "Silent-Vibrate Reminder",
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel_none.setSound(null, null)
                channel_none.enableVibration(false)
                channel_none.vibrationPattern = longArrayOf(0)
                checkNotNull(mNotificationManager)
                mBuilder.setChannelId(NOTIFICATION_SILENT_VIBRATE_CHANNEL_ID)
                mNotificationManager.createNotificationChannel(channel_none)
            }
        }
        checkNotNull(mNotificationManager)
        mNotificationManager.notify(99,  /* Request Code */mBuilder.build())
    }

    private val sound: Uri
        get() {
            return AppUtils.getSound(mContext)
        }

    private fun reachedDailyGoal(): Boolean {

        if (SharedPreferencesManager.totalIntake == 0f) {
            AppUtils.DAILY_WATER_VALUE = 2500f
        } else {
            AppUtils.DAILY_WATER_VALUE = SharedPreferencesManager.totalIntake
        }

        val arr_data = sqliteHelper!!.
            getdata("stats", "n_date ='" + 
                    AppUtils.getCurrentDate("dd-MM-yyyy") + "'")

        var drink_water = 0f
        for (k in arr_data.indices) {
            drink_water += if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                arr_data[k]["n_intook"]!!
                .toDouble().toFloat()
            else arr_data[k]["n_intook_OZ"]!!.toDouble().toFloat()
        }

        return if (drink_water >= AppUtils.DAILY_WATER_VALUE) true
        else false
    }

    @SuppressLint("WrongConstant")
    fun get_today_report(): String {

        if (SharedPreferencesManager.totalIntake == 0f) {
            AppUtils.DAILY_WATER_VALUE = 2500f
        } else {
            AppUtils.DAILY_WATER_VALUE = SharedPreferencesManager.totalIntake
        }

        if (AppUtils.checkBlankData("" + SharedPreferencesManager.unitString)) {
            AppUtils.WATER_UNIT_VALUE = "ml"
        } else {
            AppUtils.WATER_UNIT_VALUE = SharedPreferencesManager.unitString
        }

        val arr_data =
            sqliteHelper!!.getdata("stats", "n_date ='" +
                    AppUtils.getCurrentDate("dd-MM-yyyy") + "'")

        var drink_water = 0f
        for (k in arr_data.indices) {
            drink_water += if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                arr_data[k]["n_intook"]!!
                .toDouble().toFloat()
            else arr_data[k]["n_intook_OZ"]!!.toDouble().toFloat()
        }

        return mContext.resources.getString(R.string.str_have_u_had_any_water_yet)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "Default"
        private const val NOTIFICATION_CHANNEL_NAME = "Default"
        private const val NOTIFICATION_SILENT_CHANNEL_ID = "10002"
        private const val NOTIFICATION_VIBRATE_CHANNEL_ID = "10003"
        private const val NOTIFICATION_SILENT_VIBRATE_CHANNEL_ID = "10004"
    }
}