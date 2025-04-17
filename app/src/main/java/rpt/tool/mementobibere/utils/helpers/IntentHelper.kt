package rpt.tool.mementobibere.utils.helpers


import android.content.Context
import android.content.Intent
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.log.e


class IntentHelper(mContext: Context) {
    var mContext: Context? = mContext

    fun ShareText(title: String?, subject: String?) {
        try {
            val share = Intent(Intent.ACTION_SEND)
            share.setType("text/plain")
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            share.putExtra(Intent.EXTRA_SUBJECT, title) //
            share.putExtra(Intent.EXTRA_TEXT, subject)
            mContext!!.startActivity(Intent.createChooser(share, AppUtils.general_share_title))
        } catch (e: Exception) {
            e.message?.let { e(Throwable(e), it) }
        }
    }

}