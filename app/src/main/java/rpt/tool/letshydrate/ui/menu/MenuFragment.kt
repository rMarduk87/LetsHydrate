package rpt.tool.letshydrate.ui.menu

import android.os.Bundle
import android.view.View
import rpt.tool.letshydrate.BaseFragment
import rpt.tool.letshydrate.databinding.FragmentEmptyBinding
import rpt.tool.letshydrate.utils.managers.SharedPreferencesManager
import rpt.tool.letshydrate.utils.navigation.safeNavController
import rpt.tool.letshydrate.utils.navigation.safeNavigate

class MenuFragment:BaseFragment<FragmentEmptyBinding>(FragmentEmptyBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(SharedPreferencesManager.menu){
            1-> safeNavController?.safeNavigate(MenuFragmentDirections.
            actionMenuFragmentToHistoryFragment())
            2-> safeNavController?.safeNavigate(MenuFragmentDirections.
            actionMenuFragmentToStatsFragment())
            3-> safeNavController?.safeNavigate(MenuFragmentDirections
                .actionMenuFragmentToSettingsFragment())
            4-> safeNavController?.safeNavigate(MenuFragmentDirections
                .actionMenuFragmentToFaqFragment())
            5-> safeNavController?.safeNavigate(MenuFragmentDirections.
            actionMenuFragmentToProfileFragment())
            6-> safeNavController?.safeNavigate(MenuFragmentDirections
                .actionMenuFragmentToReachedGoalFragment())
            7-> safeNavController?.safeNavigate(MenuFragmentDirections.
            actionMenuFragmentToAdvancedReminderFragment())
        }
    }
}