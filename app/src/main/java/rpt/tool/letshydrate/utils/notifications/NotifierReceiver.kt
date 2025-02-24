package rpt.tool.letshydrate.utils.notifications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import rpt.tool.letshydrate.R
import rpt.tool.letshydrate.utils.AppUtils
import rpt.tool.letshydrate.utils.helpers.NotificationHelper
import rpt.tool.letshydrate.utils.managers.SharedPreferencesManager

class NotifierReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val notificationsTone = AppUtils.getSound(context)

        val title = context.resources.getString(R.string.app_name)
        val messageToShow = SharedPreferencesManager.notificationMsg


        /* Notify */
        val nHelper = NotificationHelper(context)
        @SuppressLint("ResourceType") val nBuilder = messageToShow.let {
            nHelper
                .getNotification(title, it, notificationsTone)
        }
        nHelper.notify(1, nBuilder)

    }
}