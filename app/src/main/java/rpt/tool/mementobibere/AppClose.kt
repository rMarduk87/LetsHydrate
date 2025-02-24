package rpt.tool.mementobibere

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle


class AppClose : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        finishAndRemoveTask()
    }

    companion object {
        fun exitApplication(context: Context) {
            val intent = Intent(context, AppClose::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

            context.startActivity(intent)
        }
    }
}