package rpt.tool.mementobibere.utils.view.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import rpt.tool.mementobibere.ui.userinfo.InitUserInfoEightFragment
import rpt.tool.mementobibere.ui.userinfo.InitUserInfoFiveFragment
import rpt.tool.mementobibere.ui.userinfo.InitUserInfoFourFragment
import rpt.tool.mementobibere.ui.userinfo.InitUserInfoSevenFragment
import rpt.tool.mementobibere.ui.userinfo.InitUserInfoSixFragment
import rpt.tool.mementobibere.ui.userinfo.InitUserInfoThreeFragment
import rpt.tool.mementobibere.ui.userinfo.InitUserInfoTwoFragment

class InitUserInfoPagerAdapter(fm: FragmentManager, context: Context) :
    FragmentStatePagerAdapter(fm) {
    var tab2Fragment: InitUserInfoTwoFragment =
        InitUserInfoTwoFragment()
    var tab3Fragment: InitUserInfoThreeFragment =
        InitUserInfoThreeFragment()
    var tab4Fragment: InitUserInfoFourFragment =
        InitUserInfoFourFragment()
    var tab5Fragment: InitUserInfoFiveFragment =
        InitUserInfoFiveFragment()
    var tab6Fragment: InitUserInfoSixFragment =
        InitUserInfoSixFragment()
    var tab7Fragment: InitUserInfoSevenFragment =
        InitUserInfoSevenFragment()
    var tab8Fragment: InitUserInfoEightFragment =
        InitUserInfoEightFragment()

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