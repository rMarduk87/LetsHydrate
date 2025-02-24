package rpt.tool.mementobibere.ui.userinfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.skydoves.balloon.BalloonAlign
import com.skydoves.balloon.balloon
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentInitUserInfoNameAndGenderBinding
import rpt.tool.mementobibere.utils.balloon.migration.MigrationInfoBalloonFactory
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager


class InitUserInfoNameAndGenderFragment :
    BaseFragment<FragmentInitUserInfoNameAndGenderBinding>
        (FragmentInitUserInfoNameAndGenderBinding::inflate) {

    var isMaleGender: Boolean = true
    private val migrationBalloon by balloon<MigrationInfoBalloonFactory>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        body()
    }

    private fun body() {
        if(SharedPreferencesManager.isMigration){
            migrationBalloon.showAlign(
                align = BalloonAlign.BOTTOM,
                mainAnchor = binding.txtUserName as View,
                subAnchorList = listOf(binding.txtUserName),
            )
        }
        binding.maleBlock.setOnClickListener { setGender(true) }

        binding.femaleBlock.setOnClickListener { setGender(false) }

        binding.txtUserName.setText(SharedPreferencesManager.userName)
        setGender(SharedPreferencesManager.gender != 1)

        binding.txtUserName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                SharedPreferencesManager.userName = 
                    binding.txtUserName.getText().toString().trim { it <= ' ' }
            }
        })
        
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setGender(isMale: Boolean) {
        SharedPreferencesManager.setManuallyGoal = false

        if (isMale) {
            isMaleGender = true
            SharedPreferencesManager.gender = 0
            SharedPreferencesManager.isPregnant = false
            SharedPreferencesManager.isBreastfeeding = false

            binding.maleBlock.background = requireContext()
                .resources.getDrawable(R.drawable.rdo_gender_select)
            binding.imgMale.setImageResource(R.drawable.ic_male_selected)

            binding.femaleBlock.background = requireContext()
                .resources.getDrawable(R.drawable.rdo_gender_regular)
            binding.imgFemale.setImageResource(R.drawable.ic_female_normal)
        } else {
            isMaleGender = false

            SharedPreferencesManager.gender = 1

            binding.maleBlock.background = requireContext()
                .resources.getDrawable(R.drawable.rdo_gender_regular)
            binding.imgMale.setImageResource(R.drawable.ic_male_normal)

            binding.femaleBlock.background = requireContext()
                .resources.getDrawable(R.drawable.rdo_gender_select)
            binding.imgFemale.setImageResource(R.drawable.ic_female_selected)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.txtUserName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                SharedPreferencesManager.userName =
                    binding.txtUserName.getText().toString().trim { it <= ' ' }
            }
        })
    }

}