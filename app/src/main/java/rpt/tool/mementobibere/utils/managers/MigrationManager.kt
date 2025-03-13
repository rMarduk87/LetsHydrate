package rpt.tool.mementobibere.utils.managers

import android.content.ContentValues
import android.content.Context
import com.google.android.play.core.assetpacks.dh
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.managers.MyAlarmManager.cancelRecurringAlarm
import rpt.tool.mementobibere.utils.managers.MyAlarmManager.scheduleAutoRecurringAlarm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MigrationManager {
    fun migrate() {
        if(SharedPreferencesManager.isMigration){
            val weight = SharedPreferencesManager.weight.toString()
            val climate = SharedPreferencesManager.climate
            val workType = SharedPreferencesManager.workType
            val totalIntake = SharedPreferencesManager.totalIntake

            SharedPreferencesManager.personWeight = weight
            SharedPreferencesManager.personWeightUnit = SharedPreferencesManager.weightUnit==0
            SharedPreferencesManager.unitString = if (SharedPreferencesManager.personWeightUnit)
                "ml" else "fl oz"
            SharedPreferencesManager.climate = convertClimate(climate)
            SharedPreferencesManager.workType = convertWorkType(workType)
            AppUtils.DAILY_WATER_VALUE = totalIntake
            AppUtils.WATER_UNIT = SharedPreferencesManager.unitString
            SharedPreferencesManager.hideWelcomeScreen = true
            removeShared()
        }
    }

    private fun removeShared() {
        SharedPreferencesManager.removeShared(AppUtils.THEME_KEY)
        SharedPreferencesManager.removeShared(AppUtils.UNIT_KEY)
        SharedPreferencesManager.removeShared(AppUtils.UNIT_NEW_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_50_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_100_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_150_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_200_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_250_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_300_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_350_KEY)
        SharedPreferencesManager.removeShared(AppUtils.FIRST_RUN_KEY)
        SharedPreferencesManager.removeShared(AppUtils.LAST_INTOOK_KEY)
        SharedPreferencesManager.removeShared(AppUtils.NO_UPDATE_UNIT)
        SharedPreferencesManager.removeShared(AppUtils.RESET_NOTIFICATION_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SET_GENDER_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SET_BLOOD_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SET_NEW_WORK_TYPE_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SET_CLIMATE_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SET_WEIGHT_UNIT)
        SharedPreferencesManager.removeShared(AppUtils.START_TUTORIAL_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SEE_SPLASH_KEY)
        SharedPreferencesManager.removeShared(AppUtils.STAT_IS_MONTH_KEY)
        SharedPreferencesManager.removeShared(AppUtils.INDEX_MONTH_KEY)
        SharedPreferencesManager.removeShared(AppUtils.INDEX_YEAR_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SEE_TIPS_KEY)
        SharedPreferencesManager.removeShared(AppUtils.NOTIFICATION_TONE_URI_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SLEEPING_TIME_KEY)
        SharedPreferencesManager.removeShared(AppUtils.WAKEUP_TIME_KEY)
    }

    private fun convertWorkType(workType: Int): Int {
        if(workType==0){
            return  0
        }
        return 1
    }

    private fun convertClimate(climate: Int): Int {
        var weather = 0
        when(climate){
            0->weather = 3 //from cold to snow
            1->weather = 1 //from fresh to cloudy
            2->weather = 0 //from mild to sunny
            3->weather = 0 //from torrid to sunny
        }

        return weather
    }

    fun setAlarm(context:Context) {
        val sqliteHelper = SqliteHelper(context)
        val minute_interval: Float = SharedPreferencesManager.notificationFreq

        if (AppUtils.checkBlankData(SharedPreferencesManager.wakeUpTimeNew) ||
            AppUtils.checkBlankData(SharedPreferencesManager.bedTime)
        ) {
            return
        } else {
            val startTime: Calendar = Calendar.getInstance(Locale.getDefault())
            startTime.set(Calendar.HOUR_OF_DAY, SharedPreferencesManager.wakeUpTimeHour)
            startTime.set(Calendar.MINUTE, SharedPreferencesManager.wakeUpTimeMinute)
            startTime.set(Calendar.SECOND, 0)

            val endTime: Calendar = Calendar.getInstance(Locale.getDefault())
            endTime.set(Calendar.HOUR_OF_DAY, SharedPreferencesManager.bedTimeHour)
            endTime.set(Calendar.MINUTE, SharedPreferencesManager.bedTimeMinute)
            endTime.set(Calendar.SECOND, 0)

            if (isNextDayEnd()) endTime.add(Calendar.DATE, 1)

            if (startTime.timeInMillis < endTime.timeInMillis) {
                deleteAutoAlarm(true,sqliteHelper,context)

                var _id = System.currentTimeMillis().toInt()


                val initialValues = ContentValues()
                initialValues.put(
                    "alarm_time",
                    "" + SharedPreferencesManager.wakeUpTimeNew + " - " + 
                            SharedPreferencesManager.bedTime
                )
                initialValues.put("alarm_id", "" + _id)
                initialValues.put("alarm_type", "R")
                initialValues.put("alarm_interval", "" + minute_interval)
                sqliteHelper.insert("alarm", initialValues)

                val getLastId: String = sqliteHelper.getLastId("alarm")

                while (startTime.timeInMillis <= endTime.timeInMillis) {
                    d(
                        "ALARMTIME  : ",
                        "" + startTime.get(Calendar.HOUR_OF_DAY) + ":" + startTime.get(Calendar.MINUTE) + ":" + startTime.get(
                            Calendar.SECOND
                        )
                    )

                    try {
                        _id = System.currentTimeMillis().toInt()

                        var formatedDate = ""
                        val sdf: SimpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        val sdfs: SimpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val dt: Date
                        var time = ""

                        time =
                            startTime.get(Calendar.HOUR_OF_DAY).toString() + ":" +
                                    startTime.get(Calendar.MINUTE) + ":" + startTime.get(
                                Calendar.SECOND
                            )
                        dt = sdf.parse(time)!!
                        formatedDate = sdfs.format(dt)

                        if (!sqliteHelper.isExists(
                                "alarm",
                                "alarm_time='$formatedDate'"
                            ) && !sqliteHelper.isExists(
                                "sub_alarm",
                                "alarm_time='$formatedDate'"
                            )
                        ) {
                            scheduleAutoRecurringAlarm(context, startTime, _id)

                            val initialValues2 = ContentValues()
                            initialValues2.put("alarm_time", "" + formatedDate)
                            initialValues2.put("alarm_id", "" + _id)
                            initialValues2.put("super_id", "" + getLastId)
                            sqliteHelper.insert("sub_alarm", initialValues2)


                            val _id_sunday = System.currentTimeMillis().toInt()
                            val _id_monday = System.currentTimeMillis().toInt()
                            val _id_tuesday = System.currentTimeMillis().toInt()
                            val _id_wednesday = System.currentTimeMillis().toInt()
                            val _id_thursday = System.currentTimeMillis().toInt()
                            val _id_friday = System.currentTimeMillis().toInt()
                            val _id_saturday = System.currentTimeMillis().toInt()
                            val initialValues3 = ContentValues()
                            initialValues3.put("alarm_time", "" + formatedDate)
                            initialValues3.put("alarm_id", "" + _id_sunday)
                            initialValues3.put("sunday_alarm_id", "" + _id_sunday)
                            initialValues3.put("monday_alarm_id", "" + _id_monday)
                            initialValues3.put("tuesday_alarm_id", "" + _id_tuesday)
                            initialValues3.put("wednesday_alarm_id", "" + _id_wednesday)
                            initialValues3.put("thursday_alarm_id", "" + _id_thursday)
                            initialValues3.put("friday_alarm_id", "" + _id_friday)
                            initialValues3.put("saturday_alarm_id", "" + _id_saturday)
                            initialValues3.put("alarm_type", "M")
                            initialValues3.put("alarm_interval", "0")
                            sqliteHelper.insert("alarm", initialValues3)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    startTime.add(Calendar.MINUTE, minute_interval.toInt())
                }
            } else {
                return
            }
        }
    }

    private fun isNextDayEnd(): Boolean {
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = simpleDateFormat.parse(SharedPreferencesManager.wakeUpTimeNew)
            date2 = simpleDateFormat.parse(SharedPreferencesManager.bedTime)

            return date1.time > date2.time
        } catch (e: java.lang.Exception) {
            e.message?.let { e(Throwable(e), it) }
        }

        return false
    }

    private fun deleteAutoAlarm(alsoData: Boolean, sqliteHelper: SqliteHelper, context: Context) {
        val arr_data: ArrayList<HashMap<String, String>> = sqliteHelper.getdata("alarm")

        for (k in arr_data.indices) {
            cancelRecurringAlarm(context, arr_data[k]["alarm_id"]!!.toInt())

            val arr_data2: ArrayList<HashMap<String, String>> =
                sqliteHelper.getdata("sub_alarm", "super_id=" + arr_data[k]["id"])
            for (j in arr_data2.indices) cancelRecurringAlarm(
                context, arr_data2[j]["alarm_id"]!!
                    .toInt()
            )
        }

        if (alsoData) {
            sqliteHelper.remove("alarm")
            sqliteHelper.remove("sub_alarm")
        }
    }
}