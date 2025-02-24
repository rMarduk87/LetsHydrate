package rpt.tool.mementobibere.utils.helpers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.log.i
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import java.util.*


class NotificationHelper(val ctx: Context) {
    private var notificationManager: NotificationManager? = null

    private val CHANNEL_ONE_ID = "Default"
    private val CHANNEL_ONE_NAME = "Default"


    private fun createChannels() {
        val notificationsNewMessageRingtone = AppUtils.getSound(ctx)
        val notificationChannel = NotificationChannel(
            CHANNEL_ONE_ID,
            CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.setShowBadge(true)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        notificationChannel.setSound(notificationsNewMessageRingtone, audioAttributes)

        getManager()!!.createNotificationChannel(notificationChannel)
    }

    @SuppressLint("RemoteViewLayout")
    fun getNotification(
        title: String,
        body: String,
        notificationsTone: Uri?
    ): NotificationCompat.Builder{
        createChannels()
        val view = RemoteViews(ctx.packageName,R.layout.notification)
        view.setTextViewText(R.id.title,title)
        view.setTextViewText(R.id.text, body)
        val notification = NotificationCompat.Builder(ctx.applicationContext, CHANNEL_ONE_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    ctx.resources,
                    R.mipmap.ic_launcher
                )
            )
            .setSmallIcon(R.drawable.ic_small_icon)
            .setAutoCancel(true)

        notification.setShowWhen(true)

        notification.setSound(notificationsTone)

        val notificationIntent = Intent(ctx, MainActivity::class.java)

        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val contentIntent =
            PendingIntent.getActivity(ctx, 99, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        notification.setContentIntent(contentIntent)

        return notification
    }

    private fun shallNotify(): Boolean {
        val sqliteHelper = SqliteHelper(ctx)

        val startTimestamp = AppUtils.stringTimeToMillis(SharedPreferencesManager.wakeUpTimeNew)
        val stopTimestamp = AppUtils.stringTimeToMillis(SharedPreferencesManager.bedTime)
        var totalIntake = 0f

        totalIntake = SharedPreferencesManager.totalIntake

        if (startTimestamp == 0L || stopTimestamp == 0L || totalIntake == 0f)
            return false
        if(reachedDailyGoal(sqliteHelper) && SharedPreferencesManager.disableNotificationAtGoal)
            return false

        val percent = sqliteHelper.getIntook(AppUtils.getCurrentDate()!!) * 100 / totalIntake

        val now = Calendar.getInstance().time

        val start = Date(startTimestamp)
        val stop = Date(stopTimestamp)

        val passedSeconds = compareTimes(now, start)
        val totalSeconds = compareTimes(stop, start)

        // percentage which should have been consumed by now:
        val currentTarget = passedSeconds.toFloat() / totalSeconds.toFloat() * 100f

        val doNotDisturbOff = passedSeconds >= 0 && compareTimes(now, stop) <= 0

        val notify = doNotDisturbOff && (percent < currentTarget)
        i("Let's Hydrate",
            "notify: $notify, dndOff: $doNotDisturbOff, " +
                    "currentTarget: $currentTarget, percent: $percent"
        )
        return notify
    }

    private fun reachedDailyGoal(sqliteHelper: SqliteHelper): Boolean {

        if (SharedPreferencesManager.totalIntake == 0f) {
            AppUtils.DAILY_WATER_VALUE = 2500f
        } else {
            AppUtils.DAILY_WATER_VALUE = SharedPreferencesManager.totalIntake
        }

        val arr_data: ArrayList<HashMap<String, String>> = sqliteHelper.getdata(
            "stats",
            ("n_date ='" + AppUtils.getCurrentDate("dd-MM-yyyy")) + "'"
        )

        var drink_water = 0f
        for (k in arr_data.indices) {
            if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                    drink_water += arr_data[k]["n_intook"]!!.toFloat()
            else drink_water += arr_data[k]["n_intook_OZ"]!!.toFloat()
        }

        return if (drink_water >= AppUtils.DAILY_WATER_VALUE) true
        else false
    }

    private fun compareTimes(currentTime: Date, timeToRun: Date): Long {
        val currentCal = Calendar.getInstance()
        currentCal.time = currentTime

        val runCal = Calendar.getInstance()
        runCal.time = timeToRun
        runCal.set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH))
        runCal.set(Calendar.MONTH, currentCal.get(Calendar.MONTH))
        runCal.set(Calendar.YEAR, currentCal.get(Calendar.YEAR))

        return currentCal.timeInMillis - runCal.timeInMillis
    }

    fun notify(id: Long, notification: NotificationCompat.Builder?) {
        if (shallNotify()) {
            getManager()!!.notify(id.toInt(), notification!!.build())
        }
    }

    private fun getManager(): NotificationManager? {
        if (notificationManager == null) {
            notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager
    }
}