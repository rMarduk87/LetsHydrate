package rpt.tool.mementobibere.utils.data.appmodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class AlarmDetailsModel {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("AlarmTime")
    @Expose
    var alarmTime: String? = null

    @SerializedName("AlarmId")
    @Expose
    var alarmId: String? = null

    @SerializedName("AlarmType")
    @Expose
    var alarmType: String? = null

    @SerializedName("AlarmInterval")
    @Expose
    var alarmInterval: String? = null

    @SerializedName("AlarmSubDetails")
    @Expose
    private var alarmSubDetailsList: List<AlarmSubDetailsModel> = ArrayList()

    var alarmSubDetails: List<AlarmSubDetailsModel>
        get() = alarmSubDetailsList
        set(alarmSubDetailsList) {
            this.alarmSubDetailsList = alarmSubDetailsList
        }

    //===============
    @SerializedName("SundayAlarmId")
    @Expose
    var alarmSundayId: String? = null

    @SerializedName("MondayAlarmId")
    @Expose
    var alarmMondayId: String? = null

    @SerializedName("TuesdayAlarmId")
    @Expose
    var alarmTuesdayId: String? = null

    @SerializedName("WednesdayAlarmId")
    @Expose
    var alarmWednesdayId: String? = null

    @SerializedName("ThursdayAlarmId")
    @Expose
    var alarmThursdayId: String? = null

    @SerializedName("FridayAlarmId")
    @Expose
    var alarmFridayId: String? = null

    @SerializedName("SaturdayAlarmId")
    @Expose
    var alarmSaturdayId: String? = null


    @SerializedName("IsOff")
    @Expose
    var isOff: Int = 0

    @SerializedName("Sunday")
    @Expose
    var sunday: Int = 0

    @SerializedName("Monday")
    @Expose
    var monday: Int = 0

    @SerializedName("Tuesday")
    @Expose
    var tuesday: Int = 0

    @SerializedName("Wednesday")
    @Expose
    var wednesday: Int = 0

    @SerializedName("Thursday")
    @Expose
    var thursday: Int = 0

    @SerializedName("Friday")
    @Expose
    var friday: Int = 0

    @SerializedName("Saturday")
    @Expose
    var saturday: Int = 0
}