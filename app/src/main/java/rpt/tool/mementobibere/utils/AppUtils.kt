package rpt.tool.mementobibere.utils


import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.media.Ringtone
import android.net.ParseException
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat.getString
import androidx.core.net.toUri
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.pow


class AppUtils {



    companion object {

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

        fun lblToKg(w: Int): Int {
            return (w/2.205).toInt()
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrentDate(): String? {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MM-yyyy")
            return df.format(c)
        }

        fun getMinDate(): Long {
            val calendarTodayMinOne = Calendar.getInstance()
            calendarTodayMinOne.add(Calendar.DAY_OF_MONTH, 1)
            return calendarTodayMinOne.timeInMillis
        }

        fun checkBlankData(data: String?): Boolean {
            return data == "" || data!!.isEmpty() || data.isEmpty() || data == "null"
        }

        private fun getDaySuffix(n: Int): String {
            if (n < 1 || n > 31) return "Invalid date"
            if (n in 11..13) return "th"

            return when (n % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }
        
        @SuppressLint("SimpleDateFormat")
        fun getCurrentDate(format: String?): String {
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            val date = Date()
            return dateFormat.format(date)
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrentTime(is24TimeFormat: Boolean): String {
            val dateFormat =
                if (is24TimeFormat) SimpleDateFormat("HH:mm", Locale.getDefault())
                else SimpleDateFormat("KK:mm a", Locale.getDefault())

            val date = Date()
            return dateFormat.format(date)
        }

        @SuppressLint("SimpleDateFormat")
        fun getDate(milliSeconds: Long, dateFormat: String?): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }

        @SuppressLint("SimpleDateFormat")
        fun FormateDateFromString(
            inputFormat: String?,
            outputFormat: String?,
            inputDate: String?
        ): String {
            var parsed: Date? = null
            var outputDate = ""

            val df_input = SimpleDateFormat(inputFormat, Locale.getDefault())
            val df_output = SimpleDateFormat(outputFormat, Locale.getDefault())

            try {
                parsed = inputDate?.let { df_input.parse(it) }
                outputDate = parsed?.let { df_output.format(it) }.toString()
            } catch (e: Exception) {
                e.message?.let { e(Throwable(e), it) }
            }

            return outputDate
        }

        fun get_2_point(no: String): String {
            var no = no
            if (no.length == 1) no = "0$no"
            return no
        }


        /**
         *
         * @param value double that is formatted
         * @return double that has 1 decimal place
         */
        private fun format(value: Double): Double {
            try {
                if (value != 0.0) {
                    val df = DecimalFormat("###.##")
                    return df.format(value).replace(",", ".")
                        .replace("٫", ".").toDouble()
                } else {
                    return (-1).toDouble()
                }
            } catch (e: java.lang.Exception) {
                e.message?.let { e(Throwable(e), it) }
            }

            return (-1).toDouble()
        }

        /**
         *
         * @param lb - pounds
         * @return kg rounded to 1 decimal place
         */
        fun lbToKgConverter(lb: Double): Double {
            return format(lb * 0.453592)
            //0.45359237
        }

        /**
         *
         * @param kg - kilograms
         * @return lb rounded to 1 decimal place
         */
        fun kgToLbConverter(kg: Double): Double {
            return format(kg * 2.204624420183777)
            //2.20462262
        }


        fun feetToCmConverter(feet: Double): Double {
            return format(feet * 30)
        }

        fun getBMIKg(height: Double, weight: Double): Double {
            val meters = height / 100
            return format(weight / meters.pow(2.0))
        }


        fun getBMICategory(context: Context,bmi: Double): String {
            if (bmi < 15) {
                return getString(context,R.string.bmi_cat_1)
            }
            if (bmi < 16) {
                return getString(context,R.string.bmi_cat_2)
            }
            if (bmi < 18.5) {
                return getString(context,R.string.bmi_cat_3)
            }
            if (bmi < 25) {
                return getString(context,R.string.bmi_cat_4)
            }
            if (bmi < 30) {
                return getString(context,R.string.bmi_cat_5)
            }
            if (bmi < 35) {
                return getString(context,R.string.bmi_cat_6)
            }
            if (bmi < 40) {
                return getString(context,R.string.bmi_cat_7)
            }
            if (bmi < 45) {
                return getString(context,R.string.bmi_cat_8)
            }
            if (bmi < 50) {
                return getString(context,R.string.bmi_cat_9)
            }
            if (bmi < 60) {
                return getString(context,R.string.bmi_cat_10)
            }
            return getString(context,R.string.bmi_cat_11)
        }

        fun getSound(context: Context): Uri {
            var uri: Uri = Settings.System.DEFAULT_NOTIFICATION_URI

            d("getSound", "" + SharedPreferencesManager.reminderSound)

            when (SharedPreferencesManager.reminderSound) {
                1 -> uri =
                    (("android.resource://" + context.packageName) + "/" + R.raw.bell).toUri()
                2 -> uri =
                    (("android.resource://" + context.packageName) + "/" + R.raw.blop).toUri()
                3 -> uri =
                    ((("android.resource://" + context.packageName) + "/" + R.raw.bong)).toUri()
                4 -> uri =
                    (("android.resource://" + context.packageName) + "/" + R.raw.click).toUri()
                5 -> uri =
                    (("android.resource://" + context.packageName) + "/" + R.raw.echo_droplet).toUri()
                6 -> uri =
                    (("android.resource://" + context.packageName) + "/" + R.raw.mario_droplet).toUri()
                7 -> uri =
                    (("android.resource://" + context.packageName) + "/" + R.raw.ship_bell).toUri()
                8 -> uri =
                    (("android.resource://" + context.packageName) + "/" + R.raw.simple_droplet).toUri()
                9 -> uri =
                    (("android.resource://" + context.packageName) + "/" + R.raw.tiny_droplet).toUri()
            }

            return uri
        }

        fun calculateWaterOption(countDrinkAfterAddCurrentWater: Float, dailyWaterValue: Float): Float {
            return dailyWaterValue - countDrinkAfterAddCurrentWater
        }

        @SuppressLint("SimpleDateFormat")
        fun stringTimeToMillis(time: String?): Long {
            if (time != null) {
                if(time.isNotEmpty()){
                    val simpleDateFormat = SimpleDateFormat("HH:mm")
                    try {
                        val date = simpleDateFormat.parse(time)
                        if (date != null) {
                            return date.time
                        }

                    } catch (ex: ParseException) {
                        ex.message?.let { e(Throwable(ex), it) }
                    }
                }
            }
            return 0
        }

        fun checkIsCustom(intook: Float): Boolean {
            val list = arrayOf(50f, 100f, 150f, 200f, 250f, 300f, 500f, 600f, 700f, 800f, 900f, 1000f)
            return  !list.contains(intook)
        }

        fun get_string(id: Int,mContext: Context): String {
            return mContext.resources.getString(id)
        }

        fun get_arraylist(id: Int,mContext: Context): ArrayList<String> {
            val arr = ArrayList(listOf(*mContext.resources.getStringArray(id)))
            return arr
        }

        val APP_SHARE_URL: String = ""
        val PRIVACY_POLICY_ULR: String = "https://www.termsfeed.com/live/d1615b20-2bc9-4048-8b73-b674c2aeb1c5"
        val PRIVATE_MODE = 0

        const val UNIT_KEY: String = "current_unit"
        const val UNIT_NEW_KEY: String = "new_unit"
        const val THEME_KEY: String = "theme"
        const val USERS_SHARED_PREF = "user_pref"
        const val WEIGHT_KEY = "weight"
        const val WORK_TIME_KEY = "worktime"
        const val TOTAL_INTAKE_KEY = "totalintake"
        const val NOTIFICATION_STATUS_KEY = "notificationstatus"
        const val DISABLE_NOTIFICATION = "disable_notification_at__goal"
        const val NOTIFICATION_FREQUENCY_KEY = "notificationfrequency"
        const val NOTIFICATION_MSG_KEY = "notificationmsg"
        const val SLEEPING_TIME_KEY = "sleepingtime"
        const val WAKEUP_TIME_KEY = "wakeuptime"
        const val NOTIFICATION_TONE_URI_KEY = "notificationtone"
        const val FIRST_RUN_KEY = "firstrun"
        const val VALUE_50_KEY = "50"
        const val VALUE_100_KEY = "100"
        const val VALUE_150_KEY = "150"
        const val VALUE_200_KEY = "200"
        const val VALUE_250_KEY = "250"
        const val VALUE_300_KEY = "300"
        const val VALUE_350_KEY = "350"
        const val NO_UPDATE_UNIT = "no_update_unit"
        const val UNIT_STRING = "unit_string"
        const val WEIGHT_UNIT_KEY = "weight_unit"
        const val SET_WEIGHT_UNIT = "set_weight_unit"
        const val RESET_NOTIFICATION_KEY: String = "reset_notification"
        const val notificationId = 32194567
        const val LAST_INTOOK_KEY: String = "last_intook"
        const val SEE_SPLASH_KEY : String = "see_splash"
        const val GENDER_KEY : String = "gender"
        const val SET_GENDER_KEY : String = "set_gender"
        const val BLOOD_DONOR_KEY : String = "blood_donor"
        const val SET_BLOOD_KEY : String = "set_blood_donor"
        const val SET_NEW_WORK_TYPE_KEY : String = "set_new_work_type"
        const val CLIMATE_KEY : String = "climate"
        const val SET_CLIMATE_KEY : String = "set_climate"
        const val SEE_TIPS_KEY : String = "see_tips"
        const val START_TUTORIAL_KEY : String = "start_tutorial"
        const val STAT_IS_MONTH_KEY : String = "isMonth"
        const val INDEX_MONTH_KEY : String = "month"
        const val INDEX_YEAR_KEY : String = "year"
        const val DATE : String = "date"
        var DAILY_WATER_VALUE: Float = 0f
        var WATER_UNIT_VALUE: String = "ML"
         var WATER_UNIT: String = "water_unit"
         const val SELECTED_CONTAINER: String = "selected_container"
         const val HIDE_WELCOME_SCREEN: String = "hide_welcome_screen"
         const val USER_NAME: String = "user_name"
         const val PERSON_HEIGHT: String = "person_height"
         const val PERSON_HEIGHT_UNIT: String = "person_height_unit"
         const val PERSON_WEIGHT: String = "person_weight"
         const val PERSON_WEIGHT_UNIT: String = "person_weight_unit"
         const val SET_MANUALLY_GOAL: String = "set_manually_goal"
         const val SET_MANUALLY_GOAL_VALUE: String = "set_manually_goal_value"
         const val WAKE_UP_TIME: String = "wakeup_time"
         const val WAKE_UP_TIME_HOUR: String = "wakeup_time_hour"
         const val WAKE_UP_TIME_MINUTE: String = "wakeup_time_minute"
         const val BED_TIME: String = "bed_time"
         const val BED_TIME_HOUR: String = "bed_time_hour"
         const val BED_TIME_MINUTE: String = "bed_time_minute"
         const val DISABLE_SOUND_WHEN_ADD_WATER: String = "disable_sound_when_add_water"
         const val IGNORE_NEXT_STEP: String = "ignore_next_step"
         var decimalFormat: DecimalFormat = DecimalFormat("#0.00")
         var decimalFormat2: DecimalFormat = DecimalFormat("#0.0")
         var RELOAD_DASHBOARD: Boolean = true
         const val IS_PREGNANT: String = "is_pregnant"
         const val IS_BREASTFEEDING: String = "is_breastfeeding"
         const val IS_MIGRATION: String = "is_migration"
         const val MENU: String = "navigation_menu"
         const val REMINDER_SOUND: String = "sound"
         const val IS_CHECK_BMI: String = "is_check_bmi"
         const val IS_NEW_WEIGHT_SYSTEM: String = "is_new_weight_system"
         const val MALE_WATER: Float = 35.71f
         const val ACTIVE_MALE_WATER: Float = 50.0f
         const val DEACTIVE_MALE_WATER: Float = 14.29f
         const val FEMALE_WATER: Float = 28.57f
         const val ACTIVE_FEMALE_WATER: Float = 40.0f
         const val DEACTIVE_FEMALE_WATER: Float = 11.43f
         const val PREGNANT_WATER: Float = 700.0f
         const val BREASTFEEDING_WATER: Float = 700.0f
         const val WEATHER_SUNNY: Float = 1.0f
         const val WEATHER_CLOUDY: Float = 0.85f
         const val WEATHER_RAINY: Float = 0.68f
         const val WEATHER_SNOW: Float = 0.88f
        const val DEVELOPER_MODE: Boolean = true
        var DATE_FORMAT: String = "dd-MM-yyyy"
        const val general_share_title: String = "Share"
    }
}