package rpt.tool.mementobibere.utils

import rpt.tool.mementobibere.R
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.ceil


class AppUtils {
    companion object {
        fun calculateIntake(weight: Int, workTime: Int): Double {

            return ((weight * 100 / 3.0) + (workTime / 6 * 7))

        }

        fun getCurrentOnlyDate(): String? {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MM-yyyy")
            return df.format(c)
        }

        fun getCurrentDate(): String? {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MM-yyyy-hh:mm")
            return df.format(c)
        }

        fun getCurrentDatePlusOne(): String? {
            val c = Calendar.getInstance()
            c.add(Calendar.DATE,1)
            val df = SimpleDateFormat("dd-MM-yyyy")
            return df.format(c.time)
        }

        fun mlToOzUS(ml: Float): Float {
            return ml / 29.574f
        }

        fun mlToOzUK(ml: Float) : Float{
            return ml  / 28.413f
        }

        fun ozUSToozUK(oz: Float): Float{
            return oz * 1.041f
        }

        fun ozUSToMl(oz: Float): Float{
            return ceil(oz * 29.574f)
        }

        fun ozUKToMl(oz: Float) : Float{
            return ceil(oz * 28.413f)
        }

        fun ozUKToOzUS(oz: Float): Float{
            return oz / 1.041f
        }

        fun calculateExtensions(newUnitint: Int): String {
            when(newUnitint)
            {
                0-> return "ml"
                1-> return "0z UK"
                2-> return "0z US"
            }
            return "ml"
        }

        fun firstConversion(value: Float, unit: Int): Float {
            var converted = value
            when(unit){
                1-> converted = mlToOzUK(value)
                2-> converted = mlToOzUS(value)
            }
            return converted
        }

        fun extractIntConversion(value: String?): Int {
            when(value)
            {
                "ml" -> return 0
                "0z UK" -> return 1
                "0z US" -> return 2
            }
            return 0
        }

        val UNIT_KEY: String = "current_unit"
        val UNIT_NEW_KEY: String = "new_unit"
        val THEME_KEY: String = "theme"
        val USERS_SHARED_PREF = "user_pref"
        val PRIVATE_MODE = 0
        val WEIGHT_KEY = "weight"
        val WORK_TIME_KEY = "worktime"
        val TOTAL_INTAKE_KEY = "totalintake"
        val NOTIFICATION_STATUS_KEY = "notificationstatus"
        val NOTIFICATION_FREQUENCY_KEY = "notificationfrequency"
        val NOTIFICATION_MSG_KEY = "notificationmsg"
        val SLEEPING_TIME_KEY = "sleepingtime"
        val WAKEUP_TIME_KEY = "wakeuptime"
        val START_TIME_KEY = "starttime"
        val STOP_TIME_KEY = "stoptime"
        val NOTIFICATION_TONE_URI_KEY = "notificationtone"
        val FIRST_RUN_KEY = "firstrun"
        const val intentRequestCode = 123
        val VALUE_50_KEY = "50"
        val VALUE_100_KEY = "100"
        val VALUE_150_KEY = "150"
        val VALUE_200_KEY = "200"
        val VALUE_250_KEY = "250"
        val NO_UPDATE_UNIT = "no_update_unit"
        val UNIT_STRING = "unit_string"

        enum class TypeMessage {
            NOTHING, SAVE, SLEEP
        }


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
            R.drawable.ic_info,
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

        val listIdsInfoTheme = arrayOf(
            R.id.icon_light,
            R.id.icon_dark
        )

        val listInfoTheme = arrayOf(
            R.drawable.ic_light,
            R.drawable.ic_dark

        )

        val listStringInfoTheme= arrayOf(
            R.string.light,
            R.string.dark

        )

        val listIdsInfoSystem = arrayOf(
            R.id.icon_ml,
            R.id.icon_oz_uk,
            R.id.icon_oz_us

        )

        val listInfoSystem = arrayOf(
            R.drawable.ic_ml,
            R.drawable.ic_oz_uk,
            R.drawable.ic_oz_us

        )

        val listStringInfoSystem= arrayOf(
            R.string.ml,
            R.string.oz_uk,
            R.string.oz_us
        )

        val listIdsFreq = arrayOf(
            R.id.icon_30,
            R.id.icon_45,
            R.id.icon_60

        )

        val listFreq = arrayOf(
            R.drawable.ic_30,
            R.drawable.ic_45,
            R.drawable.ic_60

        )

        val listStringFreq= arrayOf(
            R.string._30_mins,
            R.string._45_mins,
            R.string._60_mins
        )
    }
}