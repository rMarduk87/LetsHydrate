package rpt.tool.mementobibere.utils.view



import android.content.Context
import rpt.tool.mementobibere.R


object ViewUtils {
    fun isTablet(context: Context): Boolean {
        return context.resources.getBoolean(R.bool.isTablet)
    }
}