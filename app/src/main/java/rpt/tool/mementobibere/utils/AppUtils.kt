package rpt.tool.mementobibere.utils

import rpt.tool.mementobibere.R
import java.text.SimpleDateFormat
import java.util.Calendar


class AppUtils {
    companion object {
        fun calculateIntake(weight: Int, workTime: Int): Double {

            return ((weight * 100 / 3.0) + (workTime / 6 * 7))

        }

        fun getCurrentDate(): String? {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MM-yyyy")
            return df.format(c)
        }

        val THEME: String = "theme"
        val USERS_SHARED_PREF = "user_pref"
        val PRIVATE_MODE = 0
        val WEIGHT_KEY = "weight"
        val WORK_TIME_KEY = "worktime"
        val TOTAL_INTAKE = "totalintake"
        val NOTIFICATION_STATUS_KEY = "notificationstatus"
        val NOTIFICATION_FREQUENCY_KEY = "notificationfrequency"
        val NOTIFICATION_MSG_KEY = "notificationmsg"
        val SLEEPING_TIME_KEY = "sleepingtime"
        val WAKEUP_TIME = "wakeuptime"
        val NOTIFICATION_TONE_URI_KEY = "notificationtone"
        val FIRST_RUN_KEY = "firstrun"
        const val intentRequestCode = 123

        val listIds = arrayOf(
            R.id.icon_bell,
            R.id.icon_edit,
            R.id.icon_plus,
            R.id.icon_other,
            R.id.icon_stats
        )

        val listIconNotify = arrayOf(
            R.drawable.ic_bell,
            R.drawable.ic_edit,
            R.drawable.ic_plus_solid,
            R.drawable.ic_other,
            R.drawable.ic_stats
        )

        val listStringNotify = arrayOf(
            R.string.notific,
            R.string.edit,
            R.string.add,
            R.string.info,
            R.string.stats
        )

        val listIconNotNotify = arrayOf(
            R.drawable.ic_bell_disabled,
            R.drawable.ic_edit,
            R.drawable.ic_plus_solid,
            R.drawable.ic_info,
            R.drawable.ic_stats
        )

        val listStringNotNotify = arrayOf(
            R.string.notificNo,
            R.string.edit,
            R.string.add,
            R.string.info,
            R.string.stats
        )



    }
}