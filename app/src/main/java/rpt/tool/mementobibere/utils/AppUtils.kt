package rpt.tool.mementobibere.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import rpt.tool.mementobibere.utils.log.e
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class AppUtils {
    companion object {

        @SuppressLint("SimpleDateFormat")
        fun date(context: Context): String {
            val dateOfBirth = "01/03/2025"
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            var date: Date? = null
            try {
                date = sdf.parse(dateOfBirth)
            } catch (e: ParseException) {
                e.message?.let { e(Throwable(e), it) }
            }
            val dateFormat = DateFormat.getDateFormat(context)
            return date?.let { dateFormat.format(it) }.toString()
        }

        val PRIVATE_MODE = 0
        const val THEME_KEY: String = "theme"
        const val USERS_SHARED_PREF = "user_pref"
    }
}

