package rpt.tool.mementobibere.utils.notifications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.NotificationHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager


class NotifierReceiver /*: BroadcastReceiver()*/ {

    /*override fun onReceive(context: Context, intent: Intent) {

        val notificationsTone = AppUtils.getSound(context)

        val title = context.resources.getString(R.string.app_name)
        val messageToShow = SharedPreferencesManager.notificationMsg + "\n" + getToday(context)


        /* Notify */
        val nHelper = NotificationHelper(context)
        @SuppressLint("ResourceType") val nBuilder = messageToShow.let {
            nHelper
                .getNotification(title, it, notificationsTone)
        }
        nHelper.notify(1, nBuilder)

    }

    private fun getToday(context: Context): String {

        val sqliteHelper = SqliteHelper(context)

        if (SharedPreferencesManager.totalIntake == 0f) {
            AppUtils.DAILY_WATER_VALUE = 2500f
        } else {
            AppUtils.DAILY_WATER_VALUE = SharedPreferencesManager.totalIntake
        }

        if (AppUtils.checkBlankData("" + SharedPreferencesManager.unitString)) {
            AppUtils.WATER_UNIT_VALUE = "ml"
        } else {
            AppUtils.WATER_UNIT_VALUE = SharedPreferencesManager.unitString
        }

        val arr_data: ArrayList<HashMap<String, String>> = sqliteHelper.getdata(
            "stats",
            ("n_date ='" + AppUtils.getCurrentDate("dd-MM-yyyy")) + "'"
        )

        var drink_water = 0f
        for (k in arr_data.indices) {
            drink_water += if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                arr_data[k]["n_intook"]!!.toFloat()
            else arr_data[k]["n_intook_OZ"]!!.toFloat()
        }

        return context.resources.getString(R.string.str_have_u_had_any_water_yet)
    }*/
}