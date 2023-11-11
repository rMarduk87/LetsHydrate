package rpt.tool.mementobibere.utils

import android.annotation.SuppressLint
import android.content.Context
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.extensions.toPrincipalUnit
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.ceil


class AppUtils {
    companion object {
        fun calculateIntake(weight: Int, workTime: Int, weightUnit: Int): Double {

            var convertedWeight = weight.toPrincipalUnit(weightUnit)
            return ((convertedWeight * 100 / 3.0) + (workTime / 6 * 7))

        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrentOnlyDate(): String? {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MM-yyyy")
            return df.format(c)
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

        fun lblToKg(w: Int): Int {
            return (w/2.205).toInt()
        }

        fun kgToLbl(w: Int): Int {
            return (w*2.205).toInt()
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

        fun calculateExtensionsForWeight(unit: Int, context: Context): String {
            when(unit)
            {
                0-> return context.getString(R.string.kg)
                1-> return context.getString(R.string.lbl)
            }
            return context.getString(R.string.kg)
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

        fun isValidDate(wakeupTime: String, sleepingTime: String): Boolean {

            var calendarStringW = wakeupTime.split(":")
            var calendarStringS = sleepingTime.split(":")

            val calendarWake = Calendar.getInstance()
            calendarWake.set(2023,9,27,calendarStringW[0].toInt(),calendarStringW[1].toInt())

            val calendarSleep = Calendar.getInstance()
            calendarSleep.set(2023,9,27,calendarStringS[0].toInt(),calendarStringS[1].toInt())

            return !isSameDateTime(calendarWake,calendarSleep) &&
                    !isCalendar2MajorOfCalendar(calendarWake,calendarSleep)
        }

        private fun isSameDateTime(cal1: Calendar, cal2: Calendar): Boolean {
            // compare if is the same ERA, YEAR, DAY, HOUR, MINUTE and SECOND
            return cal1[Calendar.HOUR_OF_DAY] == cal2[Calendar.HOUR_OF_DAY] &&
                    cal1[Calendar.MINUTE] == cal2[Calendar.MINUTE]
        }

        private fun isCalendar2MajorOfCalendar(cal1: Calendar, cal2: Calendar): Boolean {
            // compare if is the same ERA, YEAR, DAY, HOUR, MINUTE and SECOND
            return cal2[Calendar.HOUR_OF_DAY] > cal1[Calendar.HOUR_OF_DAY] &&
                            cal2[Calendar.MINUTE] > cal1[Calendar.MINUTE]
        }

        fun getMaxWeight(weightUnit: Int): Int {
            when(weightUnit){
                0-> return 200
                1-> return 441
            }
            return 200
        }

        fun getMinWeight(weightUnit: Int): Int {
            when(weightUnit){
                0-> return 20
                1-> return 44
            }
            return 20
        }

        fun getCurrentDate(): String? {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MM-yyyy")
            return df.format(c)
        }

        fun getMaxDate(): Long {
            var calendarTodayMinOne = Calendar.getInstance()
            calendarTodayMinOne.add(Calendar.DAY_OF_MONTH, -1)
            return calendarTodayMinOne.timeInMillis
        }

        fun convertToSelected(selectedOption: Float, unit: String): Float {
            when(extractIntConversion(unit)){
                0-> return extractSelection(selectedOption)
                1-> return extractSelection(ozUKToMl(selectedOption))
                2-> return extractSelection(ozUSToMl(selectedOption))
            }
            return selectedOption
        }

        private fun extractSelection(selectedOption: Float): Float {
            return when(selectedOption){
                50f->0f
                100f->1f
                150f->2f
                200f->3f
                250f->4f
                else->5f
            }
        }


        const val UNIT_KEY: String = "current_unit"
        const val UNIT_NEW_KEY: String = "new_unit"
        const val THEME_KEY: String = "theme"
        const val USERS_SHARED_PREF = "user_pref"
        val PRIVATE_MODE = 0
        const val WEIGHT_KEY = "weight"
        const val WORK_TIME_KEY = "worktime"
        const val TOTAL_INTAKE_KEY = "totalintake"
        const val NOTIFICATION_STATUS_KEY = "notificationstatus"
        const val NOTIFICATION_FREQUENCY_KEY = "notificationfrequency"
        const val NOTIFICATION_MSG_KEY = "notificationmsg"
        const val SLEEPING_TIME_KEY = "sleepingtime"
        const val WAKEUP_TIME_KEY = "wakeuptime"
        const val START_TIME_KEY = "starttime"
        const val STOP_TIME_KEY = "stoptime"
        const val NOTIFICATION_TONE_URI_KEY = "notificationtone"
        const val FIRST_RUN_KEY = "firstrun"
        const val VALUE_50_KEY = "50"
        const val VALUE_100_KEY = "100"
        const val VALUE_150_KEY = "150"
        const val VALUE_200_KEY = "200"
        const val VALUE_250_KEY = "250"
        const val NO_UPDATE_UNIT = "no_update_unit"
        const val UNIT_STRING = "unit_string"
        const val WEIGHT_UNIT_KEY = "weight_unit"
        const val SET_WEIGHT_UNIT = "set_weight_unit"
        const val RESET_NOTIFICATION_KEY: String = "reset_notification"
        const val notificationId = 32194567
        const val LAST_INTOOK_KEY: String = "last_intook"

        enum class TypeMessage {
            NOTHING, SAVE
        }


        val listIds = arrayOf(
            R.id.icon_bell,
            R.id.icon_edit,
            R.id.icon_other,
            R.id.icon_stats
        )

        val listIconNotify = arrayOf(
            R.drawable.ic_bell,
            R.drawable.ic_edit,
            R.drawable.ic_info,
            R.drawable.ic_stats
        )

        val listStringNotify = arrayOf(
            R.string.notific,
            R.string.edit,
            R.string.info,
            R.string.stats
        )

        val listIconNotNotify = arrayOf(
            R.drawable.ic_bell_disabled,
            R.drawable.ic_edit,
            R.drawable.ic_info,
            R.drawable.ic_stats
        )

        val listStringNotNotify = arrayOf(
            R.string.notificNo,
            R.string.edit,
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

        val listIdsWeightSystem = arrayOf(
            R.id.icon_kg,
            R.id.icon_lbl
        )

        val listWeightSystem = arrayOf(
            R.drawable.ic_kg,
            R.drawable.ic_lbl
        )

        val listStringWeightSystem= arrayOf(
            R.string.kg,
            R.string.lbl
        )

        val listIdsStats = arrayOf(
            R.id.icon_all,
            R.id.icon_intook,
            R.id.icon_reach
        )

        val listIconStats = arrayOf(
            R.drawable.ic_stats,
            R.drawable.ic_intook,
            R.drawable.ic_reached
        )

        val listStringStats = arrayOf(
            R.string.all,
            R.string.intook,
            R.string.reached
        )
    }
}

