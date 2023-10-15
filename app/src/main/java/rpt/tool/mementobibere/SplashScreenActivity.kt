package rpt.tool.mementobibere

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import rpt.tool.mementobibere.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val timeoutHandler = Handler()
    private lateinit var binding : ActivitySplashScreenBinding

    companion object {

        const val ANIMATION_TIME: Long = 3300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val finalizer = Runnable {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        timeoutHandler.postDelayed(finalizer, ANIMATION_TIME)


        binding.splash.setOnClickListener{
            timeoutHandler.removeCallbacks(finalizer)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}