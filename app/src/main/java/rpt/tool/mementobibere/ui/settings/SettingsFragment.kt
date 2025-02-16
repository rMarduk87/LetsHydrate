package rpt.tool.mementobibere.ui.settings

import android.os.Bundle
import android.view.View
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentSettingsBinding
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate


class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.navigationBarColor = requireContext().resources
            .getColor(R.color.str_green_card)

        body()
    }

    private fun body() {
        binding.include1.lblToolbarTitle.text = requireContext().getString(R.string.str_settings)
        binding.include1.leftIconBlock.setOnClickListener { finish() }
        binding.include1.rightIconBlock.visibility = View.GONE

        binding.weightBlock.setOnClickListener {
            safeNavController?.safeNavigate(SettingsFragmentDirections
                .actionSettingsFragmentToProfileFragment())
        }

        binding.switchNotification.setChecked(SharedPreferencesManager.notificationStatus)

        binding.switchNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferencesManager.notificationStatus = isChecked
        }

        binding.switchSound.setChecked(SharedPreferencesManager.disableSoundWhenAddWater)

        binding.switchSound.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferencesManager.disableSoundWhenAddWater = isChecked
        }
    }

    private fun finish() {
        safeNavController?.safeNavigate(SettingsFragmentDirections
            .actionSettingsFragmentToDrinkFragment())
    }
}