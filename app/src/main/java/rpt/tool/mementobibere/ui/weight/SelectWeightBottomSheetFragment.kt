package rpt.tool.mementobibere.ui.weight

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import rpt.tool.mementobibere.BaseBottomSheetDialog
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.SelectWeightBottomSheetFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.extensions.toCalculateWeight
import java.math.RoundingMode
import java.text.DecimalFormat

class SelectWeightBottomSheetFragment:
    BaseBottomSheetDialog<SelectWeightBottomSheetFragmentBinding>(SelectWeightBottomSheetFragmentBinding::inflate) {

    private var themeInt: Int = 0
    private var weightUnit : Int = 0
    private lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        initBottomBars()
        setBackGround()
        binding.btnUpdate.setOnClickListener {

            val editor = sharedPref.edit()
            editor.putInt(AppUtils.WEIGHT_UNIT_KEY,weightUnit)
            val weight = sharedPref.getInt(AppUtils.WEIGHT_KEY, 0).toCalculateWeight(weightUnit)
            editor.putInt(AppUtils.WEIGHT_KEY,weight)
            val totalIntake = AppUtils.calculateIntake(
                weight,
                sharedPref.getInt(AppUtils.WORK_TIME_KEY, 0),
                weightUnit,
                sharedPref.getInt(AppUtils.GENDER_KEY, 0),
                sharedPref.getInt(AppUtils.CLIMATE_KEY, 0),0,
                sharedPref.getInt(AppUtils.UNIT_KEY,0)
            )
            val df = DecimalFormat("#")
            df.roundingMode = RoundingMode.CEILING
            editor.putFloat(AppUtils.TOTAL_INTAKE_KEY, df.format(totalIntake).toFloat())
            editor.putBoolean(AppUtils.SET_WEIGHT_UNIT,true)
            editor.apply()
            dismiss()
        }

    }

    private fun initBottomBars() {
        var colorString = if(themeInt==0){
            "#41B279"
        }
        else if(themeInt==1){
            "#29704D"
        }
        else if(themeInt ==2){
            "#4167B2"
        }
        else{
            "#FF6200EE"
        }
        val menu = binding.weightSystemBottomBar.menu


        for (i in AppUtils.listIdsWeightSystem.indices) {
            menu.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIdsWeightSystem[i],
                    AppUtils.listWeightSystem[i],
                    AppUtils.listStringWeightSystem[i],
                    Color.parseColor(colorString)
                )
                    .build()
            )
        }

        setWeightUnit()


        binding.weightSystemBottomBar.onItemSelectedListener = { _, i, _ ->
            when (i.id) {
                R.id.icon_kg -> weightUnit = 0
                R.id.icon_lbl -> weightUnit = 1
            }

            setWeightUnit()

        }
    }

    private fun setWeightUnit() {
        val editor = sharedPref.edit()
        editor.putInt(AppUtils.WEIGHT_UNIT_KEY,weightUnit)
        editor.apply()
    }

    private fun setBackGround() {
        when(themeInt){
            0->toLightTheme()
            1->toDarkTheme()
            2->toWaterTheme()
            3->toGrapeTheme()
        }
    }

    private fun toGrapeTheme() {
        setBackgroundColor(requireContext().getColor(R.color.purple_500))
        binding.textView7.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.btnUpdate.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.view2.
        setBackgroundColor(requireContext().getColor(R.color.gray))
        binding.weightSystemBottomBar.setBackgroundColorRes(R.color.gray)
    }

    private fun toWaterTheme() {
        setBackgroundColor(requireContext().getColor(R.color.colorSecondaryDarkW))
        binding.textView7.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.btnUpdate.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.view2.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.weightSystemBottomBar.setBackgroundColorRes(R.color.colorWhite)
    }

    private fun toDarkTheme() {
        setBackgroundColor(requireContext().getColor(R.color.darkGreen))
        binding.textView7.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.btnUpdate.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.view2.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.weightSystemBottomBar.setBackgroundColorRes(R.color.gray_btn_bg_pressed_color)
    }

    private fun toLightTheme() {
        setBackgroundColor(requireContext().getColor(R.color.colorSecondaryDark))
        binding.textView7.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.btnUpdate.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.view2.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.weightSystemBottomBar.setBackgroundColorRes(R.color.colorWhite)
    }

    private fun setBackgroundColor(color: Int) {
        binding.bottomSheetParent.setBackgroundColor(color)
    }
}