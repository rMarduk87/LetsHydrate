package rpt.tool.letshydrate

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class Application  : Application() {

    companion object {

        private lateinit var _instance: rpt.tool.letshydrate.Application

        val instance: rpt.tool.letshydrate.Application
            get() {
                return _instance
            }
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
        //Init log
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        }
    }
}