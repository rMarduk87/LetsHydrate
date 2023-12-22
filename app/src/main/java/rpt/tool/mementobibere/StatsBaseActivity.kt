package rpt.tool.mementobibere

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import rpt.tool.mementobibere.data.models.BarChartModel
import rpt.tool.mementobibere.databinding.ActivityStatsBaseBinding
import rpt.tool.mementobibere.databinding.AllStatsFragmentBinding
import rpt.tool.mementobibere.databinding.DailyStatsFragmentBinding
import rpt.tool.mementobibere.databinding.IntookCounterStatsFragmentBinding
import rpt.tool.mementobibere.databinding.ReachedGoalStatsBaseFragmentBinding
import rpt.tool.mementobibere.ui.statistics.daily.DailyViewModel
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.chart.ChartXValueFormatter
import rpt.tool.mementobibere.utils.extensions.defaultSetUp
import rpt.tool.mementobibere.utils.extensions.ifSame
import rpt.tool.mementobibere.utils.extensions.toCalculatedValueStats
import rpt.tool.mementobibere.utils.extensions.toDefaultFloatIfNull
import rpt.tool.mementobibere.utils.extensions.toExtractIntookOption
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.view.recyclerview.items.daily.DailyItem
import kotlin.math.max

class StatsBaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBaseBinding
    private var viewPagerAdapter: StatsAdapter? = null
    private lateinit var sharedPref: SharedPreferences
    private var themeInt : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setBackGround()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        viewPagerAdapter = StatsAdapter(supportFragmentManager)
        binding.statsPager.adapter = viewPagerAdapter
        binding.indicator.setViewPager(binding.statsPager)
    }

    private fun setBackGround() {
        when(themeInt){
            0->toLightTheme()
            1->toDarkTheme()
            2->toWaterTheme()
        }
    }

    private fun toWaterTheme() {
        binding.background.setBackgroundColor(getColor(R.color.colorSecondaryDarkW))
        binding.getStarted.background = getDrawable(R.drawable.walk_through_button_bg_w)
    }

    private fun toDarkTheme() {
        binding.background.setBackgroundColor(getColor(R.color.colorSecondaryDark))
        binding.getStarted.background = getDrawable(R.drawable.walk_through_button_bg_dark)
    }

    private fun toLightTheme() {
        binding.background.setBackgroundColor(getColor(R.color.colorSecondary))
        binding.getStarted.background = getDrawable(R.drawable.walk_through_button_bg)
    }

    override fun onStart() {
        super.onStart()
        binding.getStarted.setOnClickListener {

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager
                .findFragmentById(R.id.reached_goal_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private inner class StatsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return 4
        }

        override fun getItem(i: Int): Fragment {

            when (i) {
                0 -> {
                    return AllStatsView()
                }
                1->{
                    return DailyStatsView()
                }

                2 -> {
                    return IntookCounterView()
                }

                3 -> {
                    return ReachedGoalView()
                }

                else -> {
                    return AllStatsView()
                }
            }

        }
    }


    class AllStatsView : BaseFragment<AllStatsFragmentBinding>(AllStatsFragmentBinding::inflate) {
        private lateinit var sharedPref: SharedPreferences
        private lateinit var sqliteHelper: SqliteHelper
        private var totalPercentage: Float = 0f
        private var totalGlasses: Float = 0f
        private var themeInt : Int = 0
        private var unit : Int = 0

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
            themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
            unit = sharedPref.getInt(AppUtils.UNIT_KEY,0)
            super.onViewCreated(view, savedInstanceState)
            setBackGround()

            sqliteHelper = SqliteHelper(requireContext())

            val entries = ArrayList<Entry>()
            val dateArray = ArrayList<String>()

            val cursor: Cursor = sqliteHelper.getAllStats()

            if (cursor.moveToFirst()) {

                for (i in 0 until cursor.count) {
                    dateArray.add(cursor.getString(1))
                    val percent = cursor.getFloat(2).toCalculatedValueStats(
                        AppUtils.extractIntConversion(cursor.getString(4)),unit) /
                            cursor.getFloat(3).toCalculatedValueStats(
                                AppUtils.extractIntConversion(cursor.getString(4)),unit) * 100
                    totalPercentage += percent
                    totalGlasses += cursor.getInt(2)
                    entries.add(
                        Entry(
                            i.toFloat(),
                            percent
                        )
                    )
                    cursor.moveToNext()
                }

            } else {
                Toast.makeText(requireContext(), "Empty", Toast.LENGTH_LONG).show()
            }

            if (entries.isNotEmpty()) {
                if(dateArray.size > 1){
                    binding.chart.description.isEnabled = false
                    binding.chart.animateY(1000, Easing.Linear)
                    binding.chart.viewPortHandler.setMaximumScaleX(1.5f)
                    binding.chart.xAxis.setDrawGridLines(false)
                    binding.chart.xAxis.position = XAxis.XAxisPosition.TOP
                    binding.chart.xAxis.isGranularityEnabled = true
                    binding.chart.legend.isEnabled = false
                    binding.chart.fitScreen()
                    binding.chart.isAutoScaleMinMaxEnabled = true
                    binding.chart.scaleX = 1f
                    binding.chart.setPinchZoom(true)
                    binding.chart.isScaleXEnabled = true
                    binding.chart.isScaleYEnabled = false
                    binding.chart.axisLeft.textColor = Color.BLACK
                    binding.chart.xAxis.textColor = Color.BLACK
                    binding.chart.axisLeft.setDrawAxisLine(false)
                    binding.chart.xAxis.setDrawAxisLine(false)
                    binding.chart.setDrawMarkers(false)
                    binding.chart.xAxis.labelCount = 5

                    val leftAxis = binding.chart.axisLeft
                    leftAxis.axisMinimum = 0f // always start at zero
                    val maxObject: Entry = entries.maxBy { it.y } // entries is not empty here
                    leftAxis.axisMaximum = max(a = maxObject.y, b = 100f) + 15f // 15% margin on top
                    val targetLine =
                        LimitLine(
                            100f,
                            ""
                        )
                    targetLine.enableDashedLine(5f, 5f, 0f)
                    leftAxis.addLimitLine(targetLine)

                    val rightAxis = binding.chart.axisRight
                    rightAxis.setDrawGridLines(false)
                    rightAxis.setDrawZeroLine(false)
                    rightAxis.setDrawAxisLine(false)
                    rightAxis.setDrawLabels(false)

                    val dataSet =
                        LineDataSet(
                            entries,
                            "Label"
                        )
                    dataSet.setDrawCircles(false)
                    dataSet.lineWidth = 2.5f
                    dataSet.color = ContextCompat.getColor(requireContext(), setColor(themeInt))
                    dataSet.setDrawFilled(true)
                    dataSet.fillDrawable = setDrawable()
                    dataSet.setDrawValues(false)
                    dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

                    val lineData =
                        LineData(dataSet)
                    binding.chart.xAxis.valueFormatter = (ChartXValueFormatter(dateArray))
                    binding.chart.data = lineData
                    binding.chart.invalidate()
                    val layoutParams: ConstraintLayout.LayoutParams = binding.textView14.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.bottomToTop = binding.chart.id
                    binding.textView14.layoutParams = layoutParams
                    binding.chart.visibility = View.VISIBLE
                    binding.noData.visibility = View.GONE
                }
                else{
                    val layoutParams: ConstraintLayout.LayoutParams = binding.textView14.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.bottomToTop = binding.noData.id
                    binding.textView14.layoutParams = layoutParams
                    binding.chart.visibility = View.GONE
                    binding.noData.visibility = View.VISIBLE
                }

                val remaining = sharedPref.getFloat(
                    AppUtils.TOTAL_INTAKE_KEY,
                    0f
                ) - sqliteHelper.getIntook(AppUtils.getCurrentOnlyDate()!!)

                if (remaining > 0) {
                    binding.remainingIntake.text = "$remaining " + sharedPref.getString(AppUtils.UNIT_STRING,"ml")
                } else {
                    binding.remainingIntake.text = "0 " + sharedPref.getString(AppUtils.UNIT_STRING,"ml")
                }

                binding.targetIntake.text = "${sharedPref.getFloat(
                    AppUtils.TOTAL_INTAKE_KEY,
                    0f
                )
                } " + sharedPref.getString(AppUtils.UNIT_STRING,"ml")

                val percentage = sqliteHelper.getIntook(
                    AppUtils.getCurrentOnlyDate()!!
                ) * 100 / sharedPref.getFloat(
                    AppUtils.TOTAL_INTAKE_KEY,
                    0f
                )
                val intPercentage = percentage.toInt()
                binding.waterLevelViewL.centerTitle = "$intPercentage%"
                binding.waterLevelViewL.progressValue = intPercentage
                binding.waterLevelViewD!!.centerTitle = "$intPercentage%"
                binding.waterLevelViewD!!.progressValue = intPercentage
                binding.waterLevelViewW!!.centerTitle = "$intPercentage%"
                binding.waterLevelViewW!!.progressValue = intPercentage
                setBackGround()
            }

        }

        private fun setColor(themeInt: Int): Int {
            when(themeInt){
                0-> return R.color.colorSecondaryDark
                1-> return R.color.darkGreen
                2-> return R.color.colorSecondaryDarkW
            }
            return R.color.colorSecondaryDark
        }


        @SuppressLint("UseCompatLoadingForDrawables")
        private fun setDrawable(): Drawable? {
            when(themeInt){
                0-> return requireContext().getDrawable(R.drawable.graph_fill_gradiant)
                1-> return requireContext().getDrawable(R.drawable.graph_fill_gradiant_dark)
                2-> return requireContext().getDrawable(R.drawable.graph_fill_gradiant_water)
            }
            return requireContext().getDrawable(R.drawable.graph_fill_gradiant)
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private fun setBackGround() {
            when(themeInt){
                0->toLightTheme()
                1->toDarkTheme()
                2->toWaterTheme()
            }
        }

        private fun toWaterTheme() {
            binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_w)
            binding.textView8.setTextColor(requireContext().getColor(R.color.colorWhite))
            binding.waterLevelViewL.visibility = View.GONE
            binding.waterLevelViewD!!.visibility = View.GONE
            binding.waterLevelViewW!!.visibility = View.VISIBLE
            val layoutParams: ConstraintLayout.LayoutParams = binding.textView6.layoutParams
                    as ConstraintLayout.LayoutParams
            layoutParams.startToStart = binding.waterLevelViewW!!.id
            layoutParams.topToTop = binding.waterLevelViewW!!.id
            binding.textView6.layoutParams = layoutParams
        }

        private fun toDarkTheme() {
            binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_dark)
            binding.textView8.setTextColor(requireContext().getColor(R.color.colorBlack))
            binding.waterLevelViewL.visibility = View.GONE
            binding.waterLevelViewD!!.visibility = View.VISIBLE
            binding.waterLevelViewW!!.visibility = View.GONE
            val layoutParams: ConstraintLayout.LayoutParams = binding.textView6.layoutParams
                    as ConstraintLayout.LayoutParams
            layoutParams.startToStart = binding.waterLevelViewD!!.id
            layoutParams.topToTop = binding.waterLevelViewD!!.id
            binding.textView6.layoutParams = layoutParams
        }

        private fun toLightTheme() {
            binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg)
            binding.textView8.setTextColor(requireContext().getColor(R.color.colorWhite))
            binding.waterLevelViewL.visibility = View.VISIBLE
            binding.waterLevelViewD!!.visibility = View.GONE
            binding.waterLevelViewW!!.visibility = View.GONE
            val layoutParams: ConstraintLayout.LayoutParams = binding.textView6.layoutParams
                    as ConstraintLayout.LayoutParams
            layoutParams.startToStart = binding.waterLevelViewL!!.id
            layoutParams.topToTop = binding.waterLevelViewL!!.id
            binding.textView6.layoutParams = layoutParams
        }
    }

    class DailyStatsView : BaseFragment<DailyStatsFragmentBinding>(DailyStatsFragmentBinding::inflate) {

        private lateinit var sharedPref: SharedPreferences
        private var themeInt: Int = 0
        private var unit: Int = 0
        private val itemAdapter = ItemAdapter<DailyItem>()
        private val fastAdapter = FastAdapter.with(itemAdapter)

        private val viewModel: DailyViewModel by viewModels()

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            sharedPref = requireActivity().getSharedPreferences(
                AppUtils.USERS_SHARED_PREF,
                AppUtils.PRIVATE_MODE
            )
            themeInt = sharedPref.getInt(AppUtils.THEME_KEY, 0)
            unit = sharedPref.getInt(AppUtils.UNIT_KEY, 0)
            super.onViewCreated(view, savedInstanceState)
            setBackGround()

            binding.recyclerView.defaultSetUp(
                fastAdapter
            )

            addDataToListView()
        }

        private fun addDataToListView() {
            viewModel.reachedtItems.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    binding.recyclerView.visibility = View.GONE
                    binding.noData.visibility = View.VISIBLE
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.noData.visibility = View.GONE
                    itemAdapter.set(it)
                }
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private fun setBackGround() {
            when (themeInt) {
                0 -> toLightTheme()
                1 -> toDarkTheme()
                2 -> toWaterTheme()
            }
        }

        private fun toWaterTheme() {
            binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_w)
        }

        private fun toDarkTheme() {
            binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_dark)
        }

        private fun toLightTheme() {
            binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg)
        }
    }

    class IntookCounterView : BaseFragment<IntookCounterStatsFragmentBinding>(
        IntookCounterStatsFragmentBinding::inflate),
        OnChartValueSelectedListener {

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

            when(themeInt){
                0->binding.chartDaily.barsColor = resources.getColor(R.color.colorSecondary)
                1->binding.chartDaily.barsColor = resources.getColor(R.color.darkGreen)
                2->binding.chartDaily.barsColor = resources.getColor(R.color.colorSecondaryW)
            }
        }

        private fun setBottomChart() {

            val model = ArrayList<BarChartModel>()
            val cursor: Cursor = sqliteHelper.getAllIntookStats()

            if (cursor.moveToFirst()) {

                for (i in 0 until cursor.count) {
                    model.add(BarChartModel(cursor.getInt(0).toExtractIntookOption(unit),cursor.getFloat(1)))
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

            val min = sqliteHelper.getMinIntookStats()
            val max = sqliteHelper.getMaxIntookStats()

            binding.most2.text = max
                .toExtractIntookOption(unit).ifEmpty {
                    requireContext().getString(R.string.none)
                }

            binding.least.text = min
                .toExtractIntookOption(unit).ifEmpty {
                    requireContext().getString(R.string.none)
                }
                .ifSame(max.toExtractIntookOption(unit),requireContext().getString(R.string.none)){
                    requireContext().getString(R.string.none)
                }.toString()

            when(themeInt){
                0->binding.chartVarious.barsColor = resources.getColor(R.color.colorSecondary)
                1->binding.chartVarious.barsColor = resources.getColor(R.color.darkGreen)
                2->binding.chartVarious.barsColor = resources.getColor(R.color.colorSecondaryW)
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
                6.toExtractIntookOption(unit) to (model.singleOrNull { s ->
                    s.type == 6.toExtractIntookOption(
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

        @SuppressLint("UseCompatLoadingForDrawables")
        private fun setBackGround() {
            when(themeInt){
                0->toLightTheme()
                1->toDarkTheme()
                2->toWaterTheme()
            }
        }

        private fun toWaterTheme() {
            binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_w)
            binding.textView8.setTextColor(requireContext().getColor(R.color.colorWhite))
        }

        private fun toDarkTheme() {
            binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_dark)
            binding.textView8.setTextColor(requireContext().getColor(R.color.colorBlack))
        }

        private fun toLightTheme() {
            binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg)
            binding.textView8.setTextColor(requireContext().getColor(R.color.colorWhite))
        }

        override fun onValueSelected(e: Entry?, h: Highlight?) {}

        override fun onNothingSelected() {}

        companion object {
            const val animationDuration = 1000L
        }
    }

    class ReachedGoalView : BaseFragment<ReachedGoalStatsBaseFragmentBinding>(
        ReachedGoalStatsBaseFragmentBinding::inflate) {
    }
}


