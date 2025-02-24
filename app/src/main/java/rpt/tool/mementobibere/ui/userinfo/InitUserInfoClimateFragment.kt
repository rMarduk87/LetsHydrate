package rpt.tool.mementobibere.ui.userinfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentInitUserInfoClimateBinding
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager

class InitUserInfoClimateFragment :
    BaseFragment<FragmentInitUserInfoClimateBinding>(FragmentInitUserInfoClimateBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        body()
    }

    private fun body() {
        setWeather(SharedPreferencesManager.climate)
        
        binding.sunnyBlock

        binding.sunnyBlock.setOnClickListener { setWeather(0) }

        binding.cloudyBlock.setOnClickListener { setWeather(1) }

        binding.rainyBlock.setOnClickListener { setWeather(2) }

        binding.snowBlock.setOnClickListener { setWeather(3) }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setWeather(idx: Int) {
        SharedPreferencesManager.setManuallyGoal = false
        SharedPreferencesManager.climate = idx

        binding.sunnyBlock.background = if (idx == 0)
            requireContext().resources.getDrawable(R.drawable.rdo_gender_select)
        else
            requireContext().resources.getDrawable(R.drawable.rdo_gender_regular)
        binding.imgSunny.setImageResource(if (idx == 0) R.drawable.sunny_selected else R.drawable.sunny)

        binding.cloudyBlock.background = if (idx == 1)
            requireContext().resources.getDrawable(R.drawable.rdo_gender_select)
        else
            requireContext().resources.getDrawable(R.drawable.rdo_gender_regular)
        binding.imgCloudy.setImageResource(if (idx == 1) R.drawable.cloudy_selected else R.drawable.cloudy)

        binding.rainyBlock.background = if (idx == 2)
            requireContext().resources.getDrawable(R.drawable.rdo_gender_select)
        else
            requireContext().resources.getDrawable(R.drawable.rdo_gender_regular)
        binding.imgRainy.setImageResource(if (idx == 2) R.drawable.rainy_selected else R.drawable.rainy)

        binding.snowBlock.background = if (idx == 3)
            requireContext().resources.getDrawable(R.drawable.rdo_gender_select)
        else
            requireContext().resources.getDrawable(R.drawable.rdo_gender_regular)
        binding.imgSnow.setImageResource(if (idx == 3) R.drawable.snow_selected else R.drawable.snow)

    }
}