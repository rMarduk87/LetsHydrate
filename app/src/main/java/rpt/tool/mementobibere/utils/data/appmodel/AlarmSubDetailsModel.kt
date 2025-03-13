package rpt.tool.mementobibere.utils.data.appmodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class AlarmSubDetailsModel {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("AlarmTime")
    @Expose
    var alarmTime: String? = null

    @SerializedName("AlarmId")
    @Expose
    var alarmId: String? = null

    @SerializedName("SuperId")
    @Expose
    var superId: String? = null
}