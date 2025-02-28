package rpt.tool.mementobibere.utils.helpers

import android.content.Context
import rpt.tool.mementobibere.data.models.AlarmDetailsModel
import rpt.tool.mementobibere.data.models.AlarmSubDetailsModel
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.managers.MyAlarmManager.scheduleAutoRecurringAlarm
import rpt.tool.mementobibere.utils.managers.MyAlarmManager.scheduleManualRecurringAlarm
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import java.util.Calendar
import java.util.Locale


internal class AlarmHelper
    (private val mContext: Context) {

    private val alarmList: MutableList<AlarmDetailsModel> = ArrayList()
    var sqliteHelper:SqliteHelper = SqliteHelper(mContext)

    fun createAlarm() {
        loadData()
        setAlarm()
    }

    fun setAlarm() {

        for (k in alarmList.indices) {
            if (alarmList[k].alarmType.equals("M",true)
                && SharedPreferencesManager.isManualReminder
            ) {
                val hourOfDay = ("" +
                        AppUtils.FormateDateFromString(
                            "hh:mm a", "HH",
                            alarmList[k].alarmTime!!.trim()
                        )).toInt()
                val minute = ("" +
                        AppUtils.FormateDateFromString(
                            "hh:mm a", "mm",
                            alarmList[k].alarmTime!!.trim()
                        )).toInt()

                if (alarmList[k]
                        .sunday == 1
                ) scheduleManualRecurringAlarm(
                    mContext, Calendar.SUNDAY, hourOfDay, minute,
                    ("" + alarmList[k].alarmSundayId).toInt()
                )

                if (alarmList[k]
                        .monday == 1
                ) scheduleManualRecurringAlarm(
                    mContext, Calendar.MONDAY, hourOfDay, minute,
                    ("" + alarmList[k].alarmMondayId).toInt()
                )

                if (alarmList[k]
                        .tuesday == 1
                ) scheduleManualRecurringAlarm(
                    mContext, Calendar.TUESDAY, hourOfDay, minute,
                    ("" + alarmList[k].alarmTuesdayId).toInt()
                )

                if (alarmList[k]
                        .wednesday == 1
                ) scheduleManualRecurringAlarm(
                    mContext, Calendar.WEDNESDAY, hourOfDay, minute,
                    ("" + alarmList[k].alarmWednesdayId).toInt()
                )

                if (alarmList[k]
                        .thursday == 1
                ) scheduleManualRecurringAlarm(
                    mContext, Calendar.THURSDAY, hourOfDay, minute,
                    ("" + alarmList[k].alarmThursdayId).toInt()
                )

                if (alarmList[k]
                        .friday == 1
                ) scheduleManualRecurringAlarm(
                    mContext, Calendar.FRIDAY, hourOfDay, minute,
                    ("" + alarmList[k].alarmFridayId).toInt()
                )

                if (alarmList[k]
                        .saturday == 1
                ) scheduleManualRecurringAlarm(
                    mContext, Calendar.SATURDAY, hourOfDay, minute,
                    ("" + alarmList[k].alarmSaturdayId).toInt()
                )
            }

            val alarmSubDetailsList: List<AlarmSubDetailsModel> =
                alarmList[k].alarmSubDetails

            for (j in alarmSubDetailsList.indices) {
                if (!SharedPreferencesManager.isManualReminder) {
                    val hourOfDay = ("" +
                            AppUtils.FormateDateFromString(
                                "hh:mm a", "HH",
                                alarmSubDetailsList[j].alarmTime!!.trim()
                            )).toInt()
                    val minute = ("" +
                            AppUtils.FormateDateFromString(
                                "hh:mm a", "mm",
                                alarmSubDetailsList[j].alarmTime!!.trim()
                            )).toInt()

                    val updateTime: Calendar = Calendar.getInstance(Locale.getDefault())
                    updateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    updateTime.set(Calendar.MINUTE, minute)
                    updateTime.set(Calendar.SECOND, 0)

                    scheduleAutoRecurringAlarm(
                        mContext,
                        updateTime,
                        ("" + alarmSubDetailsList[j].alarmId).toInt()
                    )
                }
            }
        }
    }

    private fun loadData() {
        val arr_data = sqliteHelper.getdata("alarm")

        alarmList.clear()

        for (k in arr_data.indices) {
            val alarmDetails: AlarmDetailsModel = AlarmDetailsModel()
            alarmDetails.alarmId = (arr_data[k]["alarm_id"])
            alarmDetails.alarmInterval = (arr_data[k]["alarm_interval"])
            alarmDetails.alarmTime = (arr_data[k]["alarm_time"])
            alarmDetails.alarmType = (arr_data[k]["alarm_type"])
            alarmDetails.id = (arr_data[k]["id"])

            alarmDetails.alarmSundayId = (arr_data[k]["sunday_alarm_id"])
            alarmDetails.alarmMondayId = (arr_data[k]["monday_alarm_id"])
            alarmDetails.alarmTuesdayId = (arr_data[k]["tuesday_alarm_id"])
            alarmDetails.alarmWednesdayId = (arr_data[k]["wednesday_alarm_id"])
            alarmDetails.alarmThursdayId = (arr_data[k]["thursday_alarm_id"])
            alarmDetails.alarmFridayId = (arr_data[k]["friday_alarm_id"])
            alarmDetails.alarmSaturdayId = (arr_data[k]["saturday_alarm_id"])

            alarmDetails.isOff = (arr_data[k]["is_off"]!!.toInt())
            alarmDetails.sunday = (arr_data[k]["sunday"]!!.toInt())
            alarmDetails.monday = (arr_data[k]["monday"]!!.toInt())
            alarmDetails.tuesday = (arr_data[k]["tuesday"]!!.toInt())
            alarmDetails.wednesday = (arr_data[k]["wednesday"]!!.toInt())
            alarmDetails.thursday = (arr_data[k]["thursday"]!!.toInt())
            alarmDetails.friday = (arr_data[k]["friday"]!!.toInt())
            alarmDetails.saturday = (arr_data[k]["saturday"]!!.toInt())


            val alarmSubDetailsList: MutableList<AlarmSubDetailsModel> =
                ArrayList<AlarmSubDetailsModel>()

            val arr_data2 = sqliteHelper.getdata("sub_alarm", 
                "super_id" + arr_data[k]["id"])

            d("arr_data2 : ", "" + arr_data2.size)

            for (j in arr_data2.indices) {
                val alarmSubDetails: AlarmSubDetailsModel = AlarmSubDetailsModel()
                alarmSubDetails.alarmId = (arr_data2[j]["alarm_id"])
                alarmSubDetails.alarmTime = (arr_data2[j]["alarm_time"])
                alarmSubDetails.id = (arr_data2[j]["id"])
                alarmSubDetails.superId = (arr_data2[j]["super_id"])
                alarmSubDetailsList.add(alarmSubDetails)
            }

            alarmDetails.alarmSubDetails = alarmSubDetailsList

            alarmList.add(alarmDetails)
        }
    }
}