package rpt.tool.mementobibere.ui.userinfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentInitUserInfoStatusBinding
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager

class InitUserInfoStatusFragment :
    BaseFragment<FragmentInitUserInfoStatusBinding>
        (FragmentInitUserInfoStatusBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        body()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Deprecated("Deprecated in Java")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            

            if (SharedPreferencesManager.isPregnant) {
                binding.pregnantBlock.background = 
                    requireContext().resources.getDrawable(R.drawable.rdo_gender_select)
                binding.imgPregnant.setImageResource(R.drawable.pregnant_selected)
            } else {
                binding.pregnantBlock.background =
                    requireContext().resources.getDrawable(R.drawable.rdo_gender_regular)
                binding.imgPregnant.setImageResource(R.drawable.pregnant)
            }

            if (SharedPreferencesManager.isBreastfeeding) {
                binding.breastfeedingBlock.background = 
                    requireContext().resources.getDrawable(R.drawable.rdo_gender_select)
                binding.imgBreastfeeding.setImageResource(R.drawable.breastfeeding_selected)
            } else {
                binding.breastfeedingBlock.background = 
                    requireContext().resources.getDrawable(R.drawable.rdo_gender_regular)
                binding.imgBreastfeeding.setImageResource(R.drawable.breastfeeding)
            }

            if (SharedPreferencesManager.gender == 1)  // female
            {
                binding.pregnantBlock.isFocusableInTouchMode = true
                binding.pregnantBlock.isClickable = true
                binding.pregnantBlock.isFocusable = true
                binding.pregnantBlock.alpha = 1f

                for (i in 0 until binding.pregnantBlock.childCount) {
                    val child: View = binding.pregnantBlock.getChildAt(i)
                    child.isEnabled = true
                }

                binding.breastfeedingBlock.isFocusableInTouchMode = true
                binding.breastfeedingBlock.isClickable = true
                binding.breastfeedingBlock.isFocusable = true
                binding.breastfeedingBlock.alpha = 1f

                for (i in 0 until binding.breastfeedingBlock.childCount) {
                    val child: View = binding.breastfeedingBlock.getChildAt(i)
                    child.isEnabled = true
                }
            } else {

                binding.pregnantBlock.isFocusableInTouchMode = false
                binding.pregnantBlock.isClickable = false
                binding.pregnantBlock.isFocusable = false
                binding.pregnantBlock.alpha = 0.50f

                for (i in 0 until binding.pregnantBlock.childCount) {
                    val child: View = binding.pregnantBlock.getChildAt(i)
                    child.isEnabled = false
                }

                binding.breastfeedingBlock.isFocusableInTouchMode = false
                binding.breastfeedingBlock.isClickable = false
                binding.breastfeedingBlock.isFocusable = false
                binding.breastfeedingBlock.alpha = 0.50f

                for (i in 0 until binding.breastfeedingBlock.childCount) {
                    val child: View = binding.breastfeedingBlock.getChildAt(i)
                    child.isEnabled = false
                }
            }
        } else {
            //no
        }
    }
    

    private fun body() {
        setActive()
        setBreastfeeding()
        setPregnant()
        setBloodDonor()

        binding.activeBlock.setOnClickListener {
            if (SharedPreferencesManager.workType == 1)
                SharedPreferencesManager.workType = 0
            else SharedPreferencesManager.workType = 1
            setActive()
        }

        binding.pregnantBlock.setOnClickListener {
            SharedPreferencesManager.isPregnant = !SharedPreferencesManager.isPregnant
            setPregnant()
        }

        binding.breastfeedingBlock.setOnClickListener {
            SharedPreferencesManager.isBreastfeeding = !SharedPreferencesManager.isBreastfeeding
            setBreastfeeding()
        }

        binding.bloodDonorBlock.setOnClickListener{
            if(SharedPreferencesManager.bloodDonorKey == 1){
                SharedPreferencesManager.bloodDonorKey = 0
            }
            else SharedPreferencesManager.bloodDonorKey = 1
            setBloodDonor()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setActive() {
        SharedPreferencesManager.setManuallyGoal = false

        if (SharedPreferencesManager.workType == 1) {
            binding.activeBlock.background = requireContext()
                .resources.getDrawable(R.drawable.rdo_gender_select)
            binding.imgActive.setImageResource(R.drawable.active_selected)
        } else {
            binding.activeBlock.background = requireContext()
                .resources.getDrawable(R.drawable.rdo_gender_regular)
            binding.imgActive.setImageResource(R.drawable.active)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setPregnant() {
        SharedPreferencesManager.setManuallyGoal = false

        if (SharedPreferencesManager.isPregnant) {
            binding.pregnantBlock.background =
                requireContext().resources.getDrawable(R.drawable.rdo_gender_select)
            binding.imgPregnant.setImageResource(R.drawable.pregnant_selected)
        } else {
            binding.pregnantBlock.background =
                requireContext().resources.getDrawable(R.drawable.rdo_gender_regular)
            binding.imgPregnant.setImageResource(R.drawable.pregnant)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBreastfeeding() {
        SharedPreferencesManager.setManuallyGoal = false

        if (SharedPreferencesManager.isBreastfeeding) {
            binding.breastfeedingBlock.background = requireContext().resources.getDrawable(R.drawable.rdo_gender_select)
            binding.imgBreastfeeding.setImageResource(R.drawable.breastfeeding_selected)
        } else {
            binding.breastfeedingBlock.background = requireContext().resources.getDrawable(R.drawable.rdo_gender_regular)
            binding.imgBreastfeeding.setImageResource(R.drawable.breastfeeding)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBloodDonor() {
        SharedPreferencesManager.setManuallyGoal = false

        if (SharedPreferencesManager.bloodDonorKey == 1) {
            binding.bloodDonorBlock.background = requireContext()
                .resources.getDrawable(R.drawable.rdo_gender_select)
            binding.imgBloodDonor.setImageResource(R.drawable.blood_donation_selected)
        } else {
            binding.bloodDonorBlock.background = requireContext()
                .resources.getDrawable(R.drawable.rdo_gender_regular)
            binding.imgBloodDonor.setImageResource(R.drawable.blood_donation)
        }
    }
}