@file:Suppress("DEPRECATION")

package rpt.tool.mementobibere

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.skydoves.balloon.BalloonAlign
import com.skydoves.balloon.balloon
import rpt.tool.mementobibere.databinding.ActivityWalkThroughBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.balloon.walktrought.FifthWalkthroughBalloonFactory
import rpt.tool.mementobibere.utils.balloon.walktrought.FirstWalkthroughBalloonFactory
import rpt.tool.mementobibere.utils.balloon.walktrought.FourthWalkthroughBalloonFactory
import rpt.tool.mementobibere.utils.balloon.walktrought.SecondWalkthroughBalloonFactory
import rpt.tool.mementobibere.utils.balloon.walktrought.ThirdWalkthroughBalloonFactory

class WalkThroughActivity : AppCompatActivity() {


    private lateinit var binding: ActivityWalkThroughBinding
    private lateinit var sharedPref: SharedPreferences
    private val firstWalkthroughBalloon by balloon<FirstWalkthroughBalloonFactory>()
    private val secondWalkthroughBalloon by balloon<SecondWalkthroughBalloonFactory>()
    private val thirdWalkthroughBalloon by balloon<ThirdWalkthroughBalloonFactory>()
    private val fourthWalkthroughBalloon by balloon<FourthWalkthroughBalloonFactory>()
    private val fifthWalkthroughBalloon by balloon<FifthWalkthroughBalloonFactory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWalkThroughBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.mainLayout.setOnTouchListener { _, _ ->  false }
        binding.imageView2.setOnTouchListener { _, _ ->  false }
        binding.imageView3.setOnTouchListener { _, _ ->  false }
        binding.imageView4.setOnTouchListener { _, _ ->  false }
        binding.imageView5.setOnTouchListener { _, _ ->  false }
        binding.imageView6.setOnTouchListener { _, _ ->  false }
        startWalkThrough(binding.root)
    }

    private fun startWalkThrough(root: ConstraintLayout) {
        fifthWalkthroughBalloon.dismiss()
        binding.imageView2.visibility = View.VISIBLE
        binding.imageView3.visibility = View.GONE
        binding.imageView4.visibility = View.GONE
        binding.imageView5.visibility = View.GONE
        binding.imageView6.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            firstWalkthroughBalloon.showAlign(
                align = BalloonAlign.BOTTOM,
                mainAnchor = binding.imageView2 as View,
                subAnchorList = listOf(root),
            )
        }, 3000)

        Handler(Looper.getMainLooper()).postDelayed({
            firstWalkthroughBalloon.dismiss()
            goToSecond(root)
        }, 6500)
    }

    private fun goToSecond(root: ConstraintLayout) {
        binding.imageView2.visibility = View.GONE
        binding.imageView3.visibility = View.VISIBLE
        binding.imageView4.visibility = View.GONE
        binding.imageView5.visibility = View.GONE
        binding.imageView6.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            secondWalkthroughBalloon.showAlign(
                align = BalloonAlign.BOTTOM,
                mainAnchor = binding.imageView3 as View,
                subAnchorList = listOf(root),
            )
        }, 2000)

        Handler(Looper.getMainLooper()).postDelayed({
            secondWalkthroughBalloon.dismiss()
            goToThird(root)
        }, 5000)
    }

    private fun goToThird(root: ConstraintLayout) {
        binding.imageView2.visibility = View.GONE
        binding.imageView3.visibility = View.GONE
        binding.imageView4.visibility = View.VISIBLE
        binding.imageView5.visibility = View.GONE
        binding.imageView6.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            thirdWalkthroughBalloon.showAlign(
                align = BalloonAlign.BOTTOM,
                mainAnchor = binding.imageView4 as View,
                subAnchorList = listOf(root),
            )
        }, 3000)

        Handler(Looper.getMainLooper()).postDelayed({
            thirdWalkthroughBalloon.dismiss()
            goToFourth(root)
        }, 6500)
    }

    private fun goToFourth(root: ConstraintLayout) {
        binding.imageView2.visibility = View.GONE
        binding.imageView3.visibility = View.GONE
        binding.imageView4.visibility = View.GONE
        binding.imageView5.visibility = View.VISIBLE
        binding.imageView6.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            fourthWalkthroughBalloon.showAlign(
                align = BalloonAlign.BOTTOM,
                mainAnchor = binding.imageView5 as View,
                subAnchorList = listOf(root),
            )
        }, 3000)

        Handler(Looper.getMainLooper()).postDelayed({
            fourthWalkthroughBalloon.dismiss()
            goToFifth(root)
        }, 5000)
    }

    private fun goToFifth(root: ConstraintLayout) {
        binding.imageView2.visibility = View.GONE
        binding.imageView3.visibility = View.GONE
        binding.imageView4.visibility = View.GONE
        binding.imageView5.visibility = View.GONE
        binding.imageView6.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            fifthWalkthroughBalloon.showAlign(
                align = BalloonAlign.BOTTOM,
                mainAnchor = binding.imageView6 as View,
                subAnchorList = listOf(root),
            )
        }, 2500)

        Handler(Looper.getMainLooper()).postDelayed({
            fifthWalkthroughBalloon.dismiss()
            startActivity(Intent(this, InitUserInfoActivity::class.java))
            finish()
        }, 6500)
    }
}