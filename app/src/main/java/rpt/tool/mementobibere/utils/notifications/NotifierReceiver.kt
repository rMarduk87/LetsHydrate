package rpt.tool.mementobibere.utils.notifications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.NotificationHelper
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager

class NotifierReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val notificationsTone = SharedPreferencesManager.notificationTone

        val title = context.resources.getString(R.string.app_name)
        val messageToShow = SharedPreferencesManager.notificationMsg


        /* Notify */
        val nHelper = NotificationHelper(context)
        @SuppressLint("ResourceType") val nBuilder = messageToShow?.let {
            nHelper
                .getNotification(title, it, notificationsTone)
        }
        nHelper.notify(1, nBuilder)

    }
}