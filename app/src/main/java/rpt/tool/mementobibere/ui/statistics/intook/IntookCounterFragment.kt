package rpt.tool.mementobibere.ui.statistics.intook

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import github.com.st235.lib_expandablebottombar.Menu
import github.com.st235.lib_expandablebottombar.MenuItem
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.data.models.BarChartModel
import rpt.tool.mementobibere.databinding.IntookCounterStatsFragmentBinding
import rpt.tool.mementobibere.libraries.chart.data.Entry
import rpt.tool.mementobibere.libraries.chart.highlight.Highlight
import rpt.tool.mementobibere.libraries.chart.listener.OnChartValueSelectedListener
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.extensions.toDefaultFloatIfNull
import rpt.tool.mementobibere.utils.extensions.toExtractIntookOption
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate


class IntookCounterFragment:
    BaseFragment<IntookCounterStatsFragmentBinding>(IntookCounterStatsFragmentBinding::inflate),
    OnChartValueSelectedListener {

    private lateinit var bottomMenu: Menu
    private lateinit var sharedPref: SharedPreferences
    private var themeInt : Int = 0
    private var unit : Int = 0
    private lateinit var sqliteHelper: SqliteHelper


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        unit = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        super.onViewCreated(view, savedInstanceState)
        setBackGround()

        sqliteHelper = SqliteHelper(requireContext())

        binding.chartVarious.animate(listOf())
        binding.chartDaily.animate(listOf())

        setTopChart()
        setBottomChart()

        initBottomBar()

        binding.btnBack.setOnClickListener {
            safeNavController?.safeNavigate(IntookCounterFragmentDirections
                .actionIntookFragmentToDrinkFragment())
        }
    }

    private fun setTopChart() {
        val model = ArrayList<BarChartModel>()
        val cursor: Cursor = sqliteHelper.getDailyIntookStats(AppUtils.getCurrentDate()!!)

        if (cursor.moveToFirst()) {

            for (i in 0 until cursor.count) {
                model.add(BarChartModel(cursor.getInt(2).toExtractIntookOption(unit),cursor.getFloat(3)))
                cursor.moveToNext()
            }

            val entries = fromModelToListOf(model,unit)

            generateTopChart(entries)

            val layoutParams: ConstraintLayout.LayoutParams = binding.textView6.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.startToStart = binding.chartDaily.id
            binding.textView6.layoutParams = layoutParams
            binding.chartDaily.visibility = View.VISIBLE
            binding.noDataDaily.visibility = View.GONE
        }
        else{
            val layoutParams: ConstraintLayout.LayoutParams = binding.textView6.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.startToStart = binding.noDataDaily.id
            binding.textView6.layoutParams = layoutParams
            binding.chartDaily.visibility = View.GONE
            binding.noDataDaily.visibility = View.VISIBLE
        }

        binding.most.text = sqliteHelper.getMaxTodayIntookStats(AppUtils.getCurrentDate()!!)
            .toExtractIntookOption(unit).ifEmpty {
            requireContext().getString(R.string.none)
        }

        binding.last.text = if (sharedPref.getFloat(AppUtils.LAST_INTOOK_KEY,-1f) != -1f){
            "${sharedPref.getFloat(AppUtils.LAST_INTOOK_KEY,-1f).toInt().toExtractIntookOption(unit)}"
        }
        else{
            requireContext().getString(R.string.none)
        }
    }

    private fun setBottomChart() {

        val model = ArrayList<BarChartModel>()
        val cursor: Cursor = sqliteHelper.getAllIntookStats()

        if (cursor.moveToFirst()) {

            for (i in 0 until cursor.count) {
                model.add(BarChartModel(cursor.getInt(0).toExtractIntookOption(unit),cursor.getFloat(2)))
                cursor.moveToNext()
            }

            val entries = fromModelToListOf(model,unit)

            generateBottomChart(entries)

            val layoutParams: ConstraintLayout.LayoutParams = binding.textView60.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.startToStart = binding.chartVarious.id
            binding.textView60.layoutParams = layoutParams
            binding.chartVarious.visibility = View.VISIBLE
            binding.noData.visibility = View.GONE

            binding.textView60.text = requireContext().getString(R.string.summary2)
        }
        else{
            val layoutParams: ConstraintLayout.LayoutParams = binding.textView60.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.startToStart = binding.noData.id
            binding.textView60.layoutParams = layoutParams
            binding.chartVarious.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
            binding.textView60.text = requireContext().getString(R.string.summary2)
        }

        binding.most2.text = sqliteHelper.getMaxIntookStats()
            .toExtractIntookOption(unit).ifEmpty {
                requireContext().getString(R.string.none)
            }

        binding.least.text = sqliteHelper.getMinIntookStats()
            .toExtractIntookOption(unit).ifEmpty {
                requireContext().getString(R.string.none)
            }
    }

    private fun fromModelToListOf(model: ArrayList<BarChartModel>, unit: Int): List<Pair<String, Float>> {
        return listOf(
            0.toExtractIntookOption(unit) to (model.singleOrNull { s ->
                s.type == 0.toExtractIntookOption(
                    unit
                )
            }?.counter.toDefaultFloatIfNull() ) ,
            1.toExtractIntookOption(unit) to (model.singleOrNull { s ->
                s.type == 1.toExtractIntookOption(
                    unit
                )
            }?.counter.toDefaultFloatIfNull() ),
            2.toExtractIntookOption(unit) to (model.singleOrNull { s ->
                s.type == 2.toExtractIntookOption(
                    unit
                )
            }?.counter.toDefaultFloatIfNull() ),
            3.toExtractIntookOption(unit) to (model.singleOrNull { s ->
                s.type == 3.toExtractIntookOption(
                    unit
                )
            }?.counter.toDefaultFloatIfNull() ),
            4.toExtractIntookOption(unit) to (model.singleOrNull { s ->
                s.type == 4.toExtractIntookOption(
                    unit
                )
            }?.counter.toDefaultFloatIfNull() ),
            5.toExtractIntookOption(unit) to (model.singleOrNull { s ->
                s.type == 5.toExtractIntookOption(
                    unit
                )
            }?.counter.toDefaultFloatIfNull() ),
        )
    }

    private fun generateTopChart(barBottomSet: List<Pair<String, Float>>) {
        binding.chartDaily.animation.duration = animationDuration
        binding.chartDaily.animate(barBottomSet)
    }
    private fun generateBottomChart(barBottomSet: List<Pair<String, Float>>) {
        binding.chartVarious.animation.duration = animationDuration
        binding.chartVarious.animate(barBottomSet)
    }

    private fun initBottomBar() {

        bottomMenu = binding.bottomBarStats.menu

        createMenu()
    }

    private fun createMenu() {

        val colorString = if(themeInt==0){
            "#41B279"
        }
        else{
            "#29704D"
        }

        for (i in AppUtils.listIdsStats.indices) {
            bottomMenu.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIdsStats[i],
                    AppUtils.listIconStats[i],
                    AppUtils.listStringStats[i],
                    Color.parseColor(colorString)
                )
                    .build()
            )
        }



        binding.bottomBarStats.onItemSelectedListener = { _, i, _ ->
            manageListeners(i)
        }

        binding.bottomBarStats.onItemReselectedListener = { _, i, _ ->
            manageListeners(i)
        }

        bottomMenu.select(R.id.icon_intook)
    }

    private fun manageListeners(i: MenuItem) {
        when(i.id) {
            R.id.icon_all -> goToAllStats()
            R.id.icon_intook -> return
            R.id.icon_reach -> goToReachedGoalStats()
        }
    }

    private fun goToAllStats() {
        safeNavController?.safeNavigate(IntookCounterFragmentDirections
            .actionIntookFragmentToToAllstatsFragment())
    }

    private fun goToReachedGoalStats() {
        safeNavController?.safeNavigate(IntookCounterFragmentDirections
            .actionIntookFragmentToGoalFragment())
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBackGround() {
        when(themeInt){
            0->toLightTheme()
            1->toDarkTheme()
        }
    }

    private fun toDarkTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_dark)
        binding.bottomBarStats.setBackgroundColorRes(R.color.gray_btn_bg_pressed_color)
        binding.textView8.setTextColor(requireContext().getColor(R.color.colorBlack))
    }

    private fun toLightTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg)
        binding.bottomBarStats.setBackgroundColorRes(R.color.colorWhite)
        binding.textView8.setTextColor(requireContext().getColor(R.color.colorWhite))
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {}

    override fun onNothingSelected() {}

    companion object {
        const val animationDuration = 1000L
    }
}
