package rpt.tool.mementobibere

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import rpt.tool.mementobibere.databinding.ActivitySplashScreenBinding
import rpt.tool.mementobibere.ui.widget.NewAppWidget
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {


    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var sharedPref: SharedPreferences
    var handler: Handler? = null
    var runnable: Runnable? = null
    var millisecond: Int = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        val intent: Intent = Intent(this, NewAppWidget::class.java)
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(
            ComponentName(
                this,
                NewAppWidget::class.java
            )
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        this.sendBroadcast(intent)
    }

    @SuppressLint("UnsafeIntentLaunch")
    override fun onResume() {
        super.onResume()

        if (SharedPreferencesManager.totalIntake == 0f) {
            AppUtils.DAILY_WATER_VALUE = 2500f
        } else {
            AppUtils.DAILY_WATER_VALUE = SharedPreferencesManager.totalIntake
        }

        if (AppUtils.checkBlankData("" + SharedPreferencesManager.unitString)) {
            AppUtils.WATER_UNIT_VALUE = "ml"
        } else {
            AppUtils.WATER_UNIT_VALUE = SharedPreferencesManager.unitString
        }

        runnable = Runnable {
            if (SharedPreferencesManager.hideWelcomeScreen) {
                intent = Intent(this, MainActivity::class.java)
            } else {
                SharedPreferencesManager.personWeightUnit = true
                SharedPreferencesManager.personWeight = "80"
                SharedPreferencesManager.userName = ""
                intent = Intent(this, InitUserInfoActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
        handler = Handler()
        handler!!.postDelayed(runnable!!, millisecond.toLong())
    }
}