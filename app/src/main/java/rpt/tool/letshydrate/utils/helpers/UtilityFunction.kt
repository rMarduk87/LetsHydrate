package rpt.tool.letshydrate.utils.helpers

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy


class UtilityFunction
    (var mContext: Context, var act: Activity) {
    var sh: StringHelper = StringHelper(mContext, act)

    fun permission_StrictMode() {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }


    val isTablet: Boolean
        get() = (mContext.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE

}