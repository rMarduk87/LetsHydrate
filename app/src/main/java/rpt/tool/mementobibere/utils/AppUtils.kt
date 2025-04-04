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

        @SuppressLint("SimpleDateFormat")
        fun getMillisecondFromDate(givenDateString: String?, format: String?): Long {
            val sdf = SimpleDateFormat(format)
            var timeInMilliseconds: Long = 0
            try {
                val mDate = givenDateString?.let { sdf.parse(it) }
                timeInMilliseconds = mDate!!.time
                //println("Date in milli :: " + timeInMilliseconds);
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return timeInMilliseconds
        }

        fun getMillisecond(): Long {
            val cal = Calendar.getInstance(Locale.getDefault())
            cal[Calendar.HOUR] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.AM_PM] = 0
            return cal.timeInMillis
        }

        fun getCurrentGMTMillisecond(): Long {
            val current_cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault())
            return current_cal.timeInMillis
        }

        fun getMillisecond(year: Int, month: Int, day: Int): Long {
            val cal = Calendar.getInstance(Locale.getDefault())
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = month
            cal[Calendar.DAY_OF_MONTH] = day
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.AM_PM] = 0
            return cal.timeInMillis
        }

        fun getMillisecond(
            year: Int,
            month: Int,
            day: Int,
            hour: Int,
            minute: Int,
            format: Int
        ): Long {
            val cal = Calendar.getInstance(Locale.getDefault())
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = month
            cal[Calendar.DAY_OF_MONTH] = day
            cal[Calendar.HOUR] = hour
            cal[Calendar.MINUTE] = minute
            cal[Calendar.SECOND] = 0
            cal[Calendar.AM_PM] = format
            return cal.timeInMillis
        }

        fun getMillisecond(
            year: Int,
            month: Int,
            day: Int,
            hour: Int,
            minute: Int,
            format: String
        ): Long {
            val cal = Calendar.getInstance(Locale.getDefault())
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = month
            cal[Calendar.DAY_OF_MONTH] = day
            cal[Calendar.HOUR] = hour
            cal[Calendar.MINUTE] = minute
            cal[Calendar.SECOND] = 0
            if (format.uppercase(Locale.getDefault()) == "PM") cal[Calendar.AM_PM] = 1
            else cal[Calendar.AM_PM] = 0
            return cal.timeInMillis
        }

        fun getMillisecond(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
            val cal = Calendar.getInstance(Locale.getDefault())
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = month
            cal[Calendar.DAY_OF_MONTH] = day
            cal[Calendar.HOUR_OF_DAY] = hour
            cal[Calendar.MINUTE] = minute
            cal[Calendar.SECOND] = 0
            return cal.timeInMillis
        }

        fun getCurrentMillisecond(): Long {
            val cal = Calendar.getInstance(Locale.getDefault())
            return cal.timeInMillis
        }

        fun getTimeWithAP(time: String): String {
            val fformat: String
            val separated = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var fhour = ("" + separated[0]).toInt()
            val fmin = ("" + separated[1]).toInt()
            if (fhour == 0) {
                fhour += 12
                fformat = "AM"
            } else if (fhour == 12) {
                fformat = "PM"
            } else if (fhour > 12) {
                fhour -= 12
                fformat = "PM"
            } else {
                fformat = "AM"
            }

            return (get_2_point("" + fhour) + ":" + get_2_point("" + fmin)).toString() + " " + fformat
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
        fun getFullMonth(dateInString: String?, format: String?): String {
            val sdf = SimpleDateFormat(format)
            var formated = ""
            try {
                val date = dateInString?.let { sdf.parse(it) }
                formated = date?.let { SimpleDateFormat("MMMM").format(it) }.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return formated
        }

        @SuppressLint("SimpleDateFormat")
        fun getShortMonth(dateInString: String?, format: String?): String {
            val sdf = SimpleDateFormat(format)
            var formated = ""
            try {
                val date = dateInString?.let { sdf.parse(it) }
                formated = date?.let { SimpleDateFormat("MMM").format(it) }.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return formated
        }

        @SuppressLint("SimpleDateFormat")
        fun getMonth(dateInString: String?, format: String?): String {
            val sdf = SimpleDateFormat(format)
            var formated = ""
            try {
                val date = dateInString?.let { sdf.parse(it) }
                formated = date?.let { SimpleDateFormat("MM").format(it) }.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return formated
        }

        @SuppressLint("SimpleDateFormat")
        fun getDay(dateInString: String?, format: String?): String {
            val sdf = SimpleDateFormat(format)
            var formated = ""
            try {
                val date = dateInString?.let { sdf.parse(it) }
                formated = date?.let { SimpleDateFormat("dd").format(it) }.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return formated
        }

        @SuppressLint("SimpleDateFormat")
        fun getYear(dateInString: String?, format: String?): String {
            val sdf = SimpleDateFormat(format)
            var formated = ""
            try {
                val date = dateInString?.let { sdf.parse(it) }
                formated = date?.let { SimpleDateFormat("yyyy").format(it) }.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return formated
        }

        @SuppressLint("SimpleDateFormat")
        fun getDayswithPrefix(dateInString: String?, format: String?): String {
            val sdf = SimpleDateFormat(format)
            var formated = "0"
            try {
                val date = dateInString?.let { sdf.parse(it) }
                formated = date?.let { SimpleDateFormat("dd").format(it) }.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return formated + getDaySuffix(formated.toInt())
        }

        fun getCurrentDateTime(is24TimeFormat: Boolean): String {
            val dateFormat =
                if (is24TimeFormat) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                else SimpleDateFormat("yyyy-MM-dd KK:mm a", Locale.getDefault())

            val date = Date()
            return dateFormat.format(date)
        }

        @SuppressLint("SimpleDateFormat")
        fun set_format_date(year: Int, month: Int, day: Int, format: String?): String {
            val sdf = SimpleDateFormat(format)
            val formatedDate = sdf.format(Date(year, month, day))
            return formatedDate
        }

        @SuppressLint("SimpleDateFormat")
        fun getFormatDate(format: String?): String {
            //SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            val date = Date()
            return dateFormat.format(date)
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

        @SuppressLint("SimpleDateFormat")
        fun DayDifferent(str_date1: String?, str_date2: String?): Long {

            var diff: Long = 0
            var days_diff: Long = 0
            val myFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val date1 = str_date1?.let { myFormat.parse(it) }
                val date2 = str_date2?.let { myFormat.parse(it) }
                diff = date2!!.time - date1!!.time
                println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS))
                days_diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return days_diff
        }

        @SuppressLint("SimpleDateFormat")
        fun DayDifferent(str_date1: String?, str_date2: String?, format: String?): Long {
            var diff: Long = 0
            var days_diff: Long = 0
            val myFormat = SimpleDateFormat(format, Locale.getDefault())
            try {
                val date1 = str_date1?.let { myFormat.parse(it) }
                val date2 = str_date2?.let { myFormat.parse(it) }
                diff = date2!!.time - date1!!.time
                println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS))
                days_diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return days_diff
        }

        @SuppressLint("SimpleDateFormat")
        fun getDaysAgo(date: String): String {
            val dateString = date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            var convertedDate = Date()
            var serverDate = Date()

            try {
                convertedDate = dateFormat.parse(dateString)!!
                val c = Calendar.getInstance(Locale.getDefault())
                val formattedDate = dateFormat.format(c.time)
                serverDate = dateFormat.parse(formattedDate)!!
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val days1 = (serverDate.time - convertedDate.time)

            val seconds = days1 / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val months = days / 31
            val years = days / 365

            println("serverDate:" + serverDate.time)
            println("convertedDate:" + convertedDate.time)
            println("days1:$days1")
            println("seconds:$seconds")
            println("minutes:$minutes")
            println("hours:$hours")
            println("days:$days")
            println("months:$months")
            println("years:$years")

            return if (seconds < 86400)  // 24  60 60 ( less than 1 day )
            {
                "today"
            } else if (seconds < 172800)  // 48  60  60 ( less than 2 day )
            {
                "yesterday"
            } else if (seconds < 2592000)  // 30  24  60 * 60 ( less than 1 month )
            {
                "$days days ago"
            } else if (seconds < 31104000)  // 12  30  24  60  60
            {
                if (months <= 1) "one month ago" else "$months months ago"
            } else {
                if (years <= 1) "one year ago" else "$years years ago"
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun check_current_time_between_2date(start_date: String?, end_date: String?): Boolean {
            try {
                val mToday = Date()
                val sdf = SimpleDateFormat("hh:mm aa")
                val curTime = sdf.format(mToday)

                val start = start_date?.let { sdf.parse(it) }
                val end = end_date?.let { sdf.parse(it) }
                val userDate = sdf.parse(curTime)

                if (end!!.before(start)) {
                    val mCal = Calendar.getInstance(Locale.getDefault())
                    mCal.time = end
                    mCal.add(Calendar.DAY_OF_YEAR, 1)
                    end.time = mCal.timeInMillis
                }

                if (userDate != null) {
                    d("curTime", userDate.toString())
                }
                d("start", start.toString())
                d("end", end.toString())

                if (userDate != null) {
                    return userDate.after(start) && userDate.before(end)
                }
            } catch (e: Exception) {
                return false
            }
            return true
        }

        @SuppressLint("SimpleDateFormat")
        fun check_specific_time_between_2date(
            start_date: String?,
            end_date: String?,
            my_date: String?
        ): Boolean {
            try {
                val sdf = SimpleDateFormat("hh:mm aa")

                val start = start_date?.let { sdf.parse(it) }
                val end = end_date?.let { sdf.parse(it) }
                val userDate = my_date?.let { sdf.parse(it) }

                if (end!!.before(start)) {
                    val mCal = Calendar.getInstance(Locale.getDefault())
                    mCal.time = end
                    mCal.add(Calendar.DAY_OF_YEAR, 1)
                    end.time = mCal.timeInMillis
                }

                d("curTime", userDate.toString())
                d("start", start.toString())
                d("end", end.toString())

                return if (userDate === start) true
                else if (userDate!!.after(start) && userDate.before(end)) true
                else false
            } catch (e: Exception) {
                return false
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getGMTDate(dateFormat: String?): String {
            val formatter = SimpleDateFormat(dateFormat)
            val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("GMT")

            return formatter.format(cal.time)
        }

        fun get_total_days_of_month(month: Int, year: Int): Int {
            val day = 31

            if (month == 4 || month == 6 || month == 9 || month == 11) return 30
            else if (month == 2) {
                return if (year % 4 == 0) 29
                else 28
            }
            return day
        }

        @SuppressLint("SimpleDateFormat")
        fun different_time(current_time: String?, time: String?): Boolean {
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            var CurrentTime: Date? = null
            var Time: Date? = null

            try {
                CurrentTime = current_time?.let { simpleDateFormat.parse(it) }
                Time = time?.let { simpleDateFormat.parse(it) }
            } catch (e: ParseException) {
                //Some thing if its not working
            }

            val difference = Time!!.time - CurrentTime!!.time

            return difference >= 0
        }

        @SuppressLint("SimpleDateFormat")
        fun different_time(current_time: String?, time: String?, format: String?): Boolean {
            val simpleDateFormat = SimpleDateFormat(format)
            var CurrentTime: Date? = null
            var Time: Date? = null

            try {
                CurrentTime = current_time?.let { simpleDateFormat.parse(it) }
                Time = time?.let { simpleDateFormat.parse(it) }
            } catch (e: ParseException) {
                //Some thing if its not working
            }

            val difference = Time!!.time - CurrentTime!!.time

            return difference >= 0
        }

        fun get_2_point(no: String): String {
            var no = no
            if (no.length == 1) no = "0$no"
            return no
        }

        fun getTimeHour(time: String): String {
            val separated = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val fhour = ("" + separated[0]).toInt()
            return get_2_point("" + fhour)
        }

        fun getTimeMin(time: String): String {
            val separated = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val fmin = ("" + separated[1]).toInt()
            return get_2_point("" + fmin)
        }

        fun getTimeFormat(time: String): String {
            val fformat: String
            val separated = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val fhour = ("" + separated[0]).toInt()
            fformat = if (fhour == 0) {
                "AM"
            } else if (fhour == 12) {
                "PM"
            } else if (fhour > 12) {
                "PM"
            } else {
                "AM"
            }
            return "" + fformat
        }

        fun getCurrentTimeSecond(is24TimeFormat: Boolean): String {
            val dateFormat =
                if (is24TimeFormat) SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                else SimpleDateFormat("KK:mm:ss a", Locale.getDefault())

            val date = Date()
            return dateFormat.format(date)
        }

        @SuppressLint("SimpleDateFormat")
        fun getLastDateOfMonth(month: Int, year: Int, format: String?): String {
            val calendar = Calendar.getInstance(Locale.getDefault())
            // passing month-1 because 0-->jan, 1-->feb... 11-->dec
            calendar[year, month - 1] = 1
            calendar[Calendar.DATE] = calendar.getActualMaximum(Calendar.DATE)
            val date = calendar.time
            val DATE_FORMAT: DateFormat = SimpleDateFormat(format) //"MM/dd/yyyy"
            return DATE_FORMAT.format(date)
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

        fun cmToFeetConverter(cm: Double): Double {
            return format(cm / 30)
        }


        fun feetToCmConverter(feet: Double): Double {
            return format(feet * 30)
        }

        fun getBMIKg(height: Double, weight: Double): Double {
            val meters = height / 100
            return format(weight / meters.pow(2.0))
        }

        /**
         *
         * @param height in **feet**
         * @param weight in **pounds**
         * @return BMI index with 1 decimal place
         */
        fun getBMILb(height: Double, weight: Double): Double {
            val inch = (height * 12).toFloat()
            return format((weight * 703) / inch.pow(2.0f))
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


        var notification_ringtone: Ringtone? = null
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
        var share_purchase_title: String = "Share To"
        var launchables: List<ResolveInfo>? = null
        var pm: PackageManager? = null
        var launchables_sel: List<ResolveInfo>? = null
        const val general_share_title: String = "Share"
        const val PICK_CONTACT: Int = 1000

    }
}

