package rpt.tool.mementobibere

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import rpt.tool.mementobibere.databinding.ActivitySplashScreenBinding
import rpt.tool.mementobibere.utils.AppUtils

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private var time: Long = ANIMATION_TIME
    private val timeoutHandler = Handler()
    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var sharedPref: SharedPreferences

    companion object {

        const val ANIMATION_TIME: Long = 3000
        const val SHOW: Long = 25
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if(!sharedPref.getBoolean(AppUtils.SEE_SPLASH_KEY,true)){
            binding.splash.background = resources.getDrawable(R.mipmap.ic_launcher_background)
            binding.imageView.visibility = View.VISIBLE
            time = SHOW
            binding.rpt.visibility = View.INVISIBLE
        }

        val finalizer = Runnable {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        timeoutHandler.postDelayed(finalizer, time)


        binding.splash.setOnClickListener{
            timeoutHandler.removeCallbacks(finalizer)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}