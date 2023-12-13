@file:Suppress("DEPRECATION")

package rpt.tool.mementobibere

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.airbnb.lottie.LottieAnimationView
import rpt.tool.mementobibere.databinding.ActivityWalkThroughBinding
import rpt.tool.mementobibere.utils.AppUtils

class WalkThroughActivity : AppCompatActivity() {


    private var viewPagerAdapter: WalkThroughAdapter? = null
    private lateinit var binding: ActivityWalkThroughBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWalkThroughBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val setBlood = sharedPref.getBoolean(AppUtils.SET_BLOOD_KEY, false)
        val walkthrough = sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, false)

        if(!setBlood && walkthrough){
            binding.getStarted.setText(R.string.str_continue)
        }
        else if(!setBlood && !walkthrough){
            binding.getStarted.setText(R.string.get_started)
        }

        viewPagerAdapter = WalkThroughAdapter(supportFragmentManager)
        binding.walkThroughPager.adapter = viewPagerAdapter
        binding.indicator.setViewPager(binding.walkThroughPager)
    }

    override fun onStart() {
        super.onStart()
        binding.getStarted.setOnClickListener {

            val setBlood = sharedPref.getBoolean(AppUtils.SET_BLOOD_KEY, false)

            if(setBlood){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this, InitUserInfoActivity::class.java))
                finish()
            }


        }
    }

    private inner class WalkThroughAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            val setBlood = sharedPref.getBoolean(AppUtils.SET_BLOOD_KEY, false)
            val walkthrough = sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, false)
            return if(!setBlood && !walkthrough){
                5
            }
            else if(!setBlood && walkthrough){
                1
            }
            else{
                5
            }
        }

        override fun getItem(i: Int): Fragment {
            if(!sharedPref.getBoolean(AppUtils.SET_BLOOD_KEY, false) &&
                !sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, false)){
                when (i) {
                    0 -> {
                        return WalkThroughOne()
                    }

                    1 -> {
                        return WalkThroughTwo()
                    }

                    2 -> {
                        return WalkThroughThree()
                    }
                    3 -> {
                        return WalkThroughFour()
                    }

                    4 -> {
                        return WalkThroughFive(true)
                    }
                    else -> {
                        return WalkThroughOne()
                    }
                }
            }
            else if(!sharedPref.getBoolean(AppUtils.SET_BLOOD_KEY, false) &&
                sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, false)){
                return WalkThroughFive(false)
            }
            return WalkThroughOne()
        }
    }


    class WalkThroughOne : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            return inflater.inflate(R.layout.walk_through_one, container, false)

        }
    }

    class WalkThroughTwo : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            return inflater.inflate(R.layout.walk_through_two, container, false)

        }
    }

    class WalkThroughThree : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            return inflater.inflate(R.layout.walk_through_three, container, false)

        }

    }

    class WalkThroughFour : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            return inflater.inflate(R.layout.walk_through_four, container, false)

        }

    }

    class WalkThroughFive(isFirst: Boolean) : Fragment() {
        private lateinit var sharedPref: SharedPreferences
        private lateinit var btnAvis: LottieAnimationView
        private lateinit var textView4 : TextView
        private lateinit var textView40 : TextView
        private lateinit var scrollView: ScrollView
        private var bloodDonor : Int = 0
        private var _isFirst :Boolean = isFirst
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

            var view =  inflater.inflate(R.layout.walk_through_five, container, false)

            btnAvis = view.findViewById(R.id.btnAvis) as LottieAnimationView
            textView4 = view.findViewById(R.id.textView4) as TextView
            textView40 = view.findViewById(R.id.textView40) as TextView
            scrollView = view.findViewById(R.id.scrollView2) as ScrollView

            btnAvis.visibility = if(_isFirst){View.GONE}else{View.VISIBLE}

            textView4.visibility = if(_isFirst){View.GONE}else{View.VISIBLE}

            textView40.visibility = if(!_isFirst){View.GONE}else{View.VISIBLE}

            scrollView.visibility = if(_isFirst){View.GONE}else{View.VISIBLE}

            btnAvis.setOnClickListener{
                if(bloodDonor == 0){
                    bloodDonor = 1
                }
                else{
                    bloodDonor = 0
                }

                val editor = sharedPref.edit()
                editor.putInt(AppUtils.BLOOD_DONOR_KEY,bloodDonor)
                editor.putBoolean(AppUtils.SET_BLOOD_KEY, true)
                editor.apply()
            }
            return view;
        }
    }
}