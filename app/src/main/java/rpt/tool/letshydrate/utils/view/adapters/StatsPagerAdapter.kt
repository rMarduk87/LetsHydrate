package rpt.tool.letshydrate.utils.view.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import rpt.tool.letshydrate.R
import rpt.tool.letshydrate.ui.statistics.stats.MonthStatsFragment
import rpt.tool.letshydrate.ui.statistics.stats.WeekStatsFragment
import rpt.tool.letshydrate.ui.statistics.stats.YearStatsFragment

class StatsPagerAdapter(fm: FragmentManager, context: Context) :
    FragmentStatePagerAdapter(fm) {
    var tab1Fragment: WeekStatsFragment =
        WeekStatsFragment()
    var tab2Fragment: MonthStatsFragment =
        MonthStatsFragment()
    var tab3Fragment: YearStatsFragment =
        YearStatsFragment()

    var mContext: Context = context

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                tab1Fragment
            }
            1 -> {
                tab2Fragment
            }
            else -> {
                tab3Fragment
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when (position) {
            0 -> {
                title = mContext.resources.getString(R.string.str_week)
            }
            1 -> {
                title = mContext.resources.getString(R.string.str_month)
            }
            2 -> {
                title = mContext.resources.getString(R.string.str_year)
            }
        }
        return title
    }
}
