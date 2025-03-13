package rpt.tool.mementobibere.utils.newnotifications

import android.annotation.SuppressLint
import android.content.Context
import android.os.PowerManager


object WakeLocker {
    private var wakeLock: PowerManager.WakeLock? = null

    @SuppressLint("InvalidWakeLockTag")
    fun acquire(ctx: Context) {
        if (wakeLock != null) wakeLock!!.release()

        val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE, ""
        )
        wakeLock!!.acquire(10*60*1000L /*10 minutes*/)
    }

    fun release() {
        if (wakeLock != null) wakeLock!!.release()
        wakeLock = null
    }
}