package rpt.tool.letshydrate.utils.view.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import rpt.tool.letshydrate.ui.userinfo.InitUserInfoClimateFragment
import rpt.tool.letshydrate.ui.userinfo.InitUserInfoFinalFragment
import rpt.tool.letshydrate.ui.userinfo.InitUserInfoGoalCalculationFragment
import rpt.tool.letshydrate.ui.userinfo.InitUserInfoStatusFragment
import rpt.tool.letshydrate.ui.userinfo.InitUserInfoNotificationSettingsFragment
import rpt.tool.letshydrate.ui.userinfo.InitUserInfoHeightAndWeightFragment
import rpt.tool.letshydrate.ui.userinfo.InitUserInfoNameAndGenderFragment

class InitUserInfoPagerAdapter(fm: FragmentManager, context: Context) :
    FragmentStatePagerAdapter(fm) {
    var tab2Fragment: InitUserInfoNameAndGenderFragment =
        InitUserInfoNameAndGenderFragment()
    var tab3Fragment: InitUserInfoHeightAndWeightFragment =
        InitUserInfoHeightAndWeightFragment()
    var tab4Fragment: InitUserInfoGoalCalculationFragment =
        InitUserInfoGoalCalculationFragment()
    var tab5Fragment: InitUserInfoFinalFragment =
        InitUserInfoFinalFragment()
    var tab6Fragment: InitUserInfoNotificationSettingsFragment =
        InitUserInfoNotificationSettingsFragment()
    var tab7Fragment: InitUserInfoStatusFragment =
        InitUserInfoStatusFragment()
    var tab8Fragment: InitUserInfoClimateFragment =
        InitUserInfoClimateFragment()

    var mContext: Context = context

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> {
                tab3Fragment
            }
            2 -> {
                tab7Fragment
            }
            3 -> {
                tab8Fragment
            }
            4 -> {
                tab4Fragment
            }
            5 -> {
                tab6Fragment
            }
            6 -> {
                tab5Fragment
            }
            else -> {
                tab2Fragment
            }
        }
    }

    override fun getCount(): Int {
        return 7
    }
}