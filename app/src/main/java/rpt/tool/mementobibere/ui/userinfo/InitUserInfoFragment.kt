package rpt.tool.mementobibere.ui.userinfo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentInitUserInfoBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.AlertHelper
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.view.adapters.InitUserInfoPagerAdapter

@Suppress("DEPRECATION")
class InitUserInfoFragment:
    BaseFragment<FragmentInitUserInfoBinding>(FragmentInitUserInfoBinding::inflate) {


    var initUserInfoPagerAdapter: InitUserInfoPagerAdapter? = null
    var current_page_idx: Int = 0
    var max_page: Int = 7
    var alertHelper: AlertHelper? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.navigationBarColor =
            requireContext().resources.getColor(R.color.water_color)

        alertHelper = AlertHelper(requireContext())

        body()
    }

    fun body() {
        initUserInfoPagerAdapter = InitUserInfoPagerAdapter(
            requireActivity().supportFragmentManager, requireContext())
        binding.viewPager.setAdapter(initUserInfoPagerAdapter)
        binding.viewPager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                current_page_idx = position

                if (position == 0) {
                    binding.btnBack.visibility = View.GONE
                    binding.space.visibility = View.GONE
                } else {
                    binding.btnBack.visibility = View.VISIBLE
                    binding.space.visibility = View.VISIBLE
                }

                if (position == max_page - 1) {
                    binding.lblNext.text = requireActivity().getString(R.string.str_get_started)
                } else {
                    binding.lblNext.text = requireActivity().getString(R.string.str_next)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.viewPager.setOffscreenPageLimit(10)

        binding.dotsIndicator.setViewPager(binding.viewPager)

        binding.btnBack.setOnClickListener {
            if (current_page_idx > 0) {
                current_page_idx -= 1
            }
            binding.viewPager.setCurrentItem(current_page_idx)
        }
        
        binding.btnNext.setOnClickListener(View.OnClickListener {
            if (current_page_idx == 0) {
                if (AppUtils.checkBlankData(SharedPreferencesManager.userName)) {
                    alertHelper!!.customAlert(requireActivity().getString(R.string.str_your_name_validation))
                    return@OnClickListener
                }

                if (SharedPreferencesManager.userName.length < 3) {
                    alertHelper!!.customAlert(requireActivity().getString(R.string.str_valid_name_validation))
                    return@OnClickListener
                }
            }
            if (current_page_idx == 1) {
                try {
                    if (AppUtils.checkBlankData(SharedPreferencesManager.personHeight)) {
                        alertHelper!!.customAlert(requireActivity().getString(R.string.str_height_validation))
                        return@OnClickListener
                    }

                    if (AppUtils.checkBlankData(SharedPreferencesManager.personWeight)) {
                        alertHelper!!.customAlert(requireActivity().getString(R.string.str_weight_validation))
                        return@OnClickListener
                    }

                    val `val` = (SharedPreferencesManager.personWeight).toFloat()
                    if (`val` < 2) {
                        alertHelper!!.customAlert(requireActivity().getString(R.string.str_height_validation))
                        return@OnClickListener
                    }

                    val val2 = (SharedPreferencesManager.personWeight).toFloat()
                    if (val2 < 30) {
                        alertHelper!!.customAlert(requireActivity().getString(R.string.str_weight_validation))
                        return@OnClickListener
                    }
                } catch (e: Exception) {
                    e.message?.let { it1 -> e(Throwable(e), it1) }
                }
            }

            if (current_page_idx == 5) {
                if (AppUtils.checkBlankData(SharedPreferencesManager.wakeUpTimeNew) || AppUtils.checkBlankData(
                        SharedPreferencesManager.bedTime
                    )
                ) {
                    alertHelper!!.customAlert(requireActivity()
                        .getString(R.string.str_from_to_invalid_validation))
                    return@OnClickListener
                } else if (SharedPreferencesManager.ignoreNextStep) {
                    alertHelper!!.customAlert(requireActivity()
                        .getString(R.string.str_from_to_invalid_validation))
                    return@OnClickListener
                }
            }
            if (current_page_idx < max_page - 1) {
                current_page_idx += 1
                binding.viewPager.setCurrentItem(current_page_idx)
            } else {
                checkStoragePermissions()
            }
        })
    }
    

    private fun checkStoragePermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 3
            )
        } else {
            gotoHomeScreen()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            3 ->
                gotoHomeScreen()

        }
    }

    private fun gotoHomeScreen() {
        SharedPreferencesManager.hideWelcomeScreen = true
        SharedPreferencesManager.isMigration = false

        startActivity(Intent(requireActivity(), MainActivity::class.java))

    }

}