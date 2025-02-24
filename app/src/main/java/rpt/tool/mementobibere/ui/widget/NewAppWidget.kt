package rpt.tool.mementobibere.ui.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.RemoteViews
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.SelectBottleActivity
import rpt.tool.mementobibere.SplashScreenActivity
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager


class NewAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mContext: Context? = null
        var drink_water: Float = 0f
        var sqliteHelper:SqliteHelper? = null

        fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            mContext = context
            sqliteHelper = SqliteHelper(mContext!!)
            //CharSequence widgetText = context.getString(R.string.appwidget_text);
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.app_widget)
            views.setTextViewText(R.id.appwidget_text, get_today_report(sqliteHelper!!))

            views.setInt(R.id.circularProgressbar, "setMax",
                AppUtils.DAILY_WATER_VALUE.toInt())
            views.setInt(R.id.circularProgressbar, "setProgress", drink_water.toInt())

            val launchMain = Intent(context, SplashScreenActivity::class.java)
            launchMain.putExtra("from_widget", true)
            launchMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingMainIntent = PendingIntent.getActivity(context, 0, launchMain,
                PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget, pendingMainIntent)


            val launchMain2 = Intent(context, SelectBottleActivity::class.java)
            launchMain2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingMainIntent2 = PendingIntent.getActivity(context, 0, launchMain2,
                PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.add_water, pendingMainIntent2)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        @SuppressLint("WrongConstant")
        fun get_today_report(sqliteHelper: SqliteHelper): String {

            val arr_data = sqliteHelper.getdata(
                "stats",
                ("date ='" + AppUtils.getCurrentDate("dd-MM-yyyy")) + "'"
            )

            if (SharedPreferencesManager.totalIntake == 0f) {
                AppUtils.DAILY_WATER_VALUE = 2500f
            } else {
                AppUtils.DAILY_WATER_VALUE = SharedPreferencesManager.totalIntake
            }

            if (AppUtils.checkBlankData(SharedPreferencesManager.unitString)) {
                AppUtils.WATER_UNIT_VALUE = "ml"
            } else {
                AppUtils.WATER_UNIT_VALUE = SharedPreferencesManager.unitString
            }

            drink_water = 0f
            for (k in arr_data.indices) {
                drink_water += if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                    arr_data[k]["n_intook"]!!.toFloat()
                else
                    arr_data[k]["n_intook_OZ"]!!.toFloat()
            }

            return "" + (drink_water).toInt() + "/" +
                    AppUtils.DAILY_WATER_VALUE + " " + AppUtils.WATER_UNIT_VALUE

        }
    }
}