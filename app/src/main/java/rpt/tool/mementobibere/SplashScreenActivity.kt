package rpt.tool.mementobibere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import rpt.tool.mementobibere.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding

    companion object {

        const val ANIMATION_TIME: Long = 7500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(this.mainLooper).postDelayed({

            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }, ANIMATION_TIME)

    }
}