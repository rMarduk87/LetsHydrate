package rpt.tool.mementobibere.ui.statistics.stats

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentStatsBinding
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate
import rpt.tool.mementobibere.utils.view.adapters.StatsPagerAdapter


class StatsFragment : BaseFragment<FragmentStatsBinding>(FragmentStatsBinding::inflate){

    var statsPagerAdapter: StatsPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rdoWeek.setOnClickListener { binding.viewPager.setCurrentItem(0) }

        binding.rdoMonth.setOnClickListener { binding.viewPager.setCurrentItem(1) }

        binding.rdoYear.setOnClickListener { binding.viewPager.setCurrentItem(2) }

        body()
    }

    private fun body(){
        binding.include1.lblToolbarTitle.text = requireContext()
            .getString(R.string.str_drink_report)
        binding.include1.leftIconBlock.setOnClickListener { finish() }
        binding.include1.rightIconBlock.visibility = View.GONE

        binding.tabs.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        statsPagerAdapter = StatsPagerAdapter(requireActivity().supportFragmentManager, requireContext())
        binding.viewPager.setAdapter(statsPagerAdapter)
        binding.viewPager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.viewPager.setOffscreenPageLimit(5)

        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun finish() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
    }
}