package rpt.tool.mementobibere.ui.statistics.details

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import rpt.tool.mementobibere.BaseBottomSheetDialog
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.data.models.BarChartModel
import rpt.tool.mementobibere.databinding.IntookCounterStatsBottomSheetFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.extensions.toDefaultFloatIfNull
import rpt.tool.mementobibere.utils.extensions.toExtractIntookOption
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import rpt.tool.mementobibere.BaseFragment


class IntookCounterFragment :
    BaseFragment<IntookCounterStatsBottomSheetFragmentBinding>
        (IntookCounterStatsBottomSheetFragmentBinding::inflate),OnChartValueSelectedListener {

    private lateinit var sharedPref: SharedPreferences
    private var themeInt : Int = 0
    private var unit : Int = 0
    private lateinit var sqliteHelper: SqliteHelper
    private var date : String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        unit = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        date = sharedPref.getString(AppUtils.DATE,AppUtils.getCurrentDate()!!)!!
        super.onViewCreated(view, savedInstanceState)
        setBackGround()

        sqliteHelper = SqliteHelper(requireContext())
        binding.chartDaily.animate(listOf())

        val remaining = sqliteHelper.getTotalIntakeValue(date) - sqliteHelper.getIntook(date)

        if (remaining > 0) {
            binding.remainingIntake!!.text = "$remaining " + sharedPref.getString(AppUtils.UNIT_STRING,"ml")
        } else {
            binding.remainingIntake!!.text = "0 " + sharedPref.getString(AppUtils.UNIT_STRING,"ml")
        }

        binding.targetIntake!!.text = "${sqliteHelper.getTotalIntakeValue(date)
        } " + sharedPref.getString(AppUtils.UNIT_STRING,"ml")

        val percentage = sqliteHelper.getIntook(date) * 100 / sqliteHelper.getTotalIntakeValue(date)
        val intPercentage = percentage.toInt()
        binding.waterLevelViewL!!.centerTitle = "$intPercentage%"
        binding.waterLevelViewL!!.progressValue = intPercentage
        binding.waterLevelViewD!!.centerTitle = "$intPercentage%"
        binding.waterLevelViewD!!.progressValue = intPercentage
        binding.waterLevelViewW!!.centerTitle = "$intPercentage%"
        binding.waterLevelViewW!!.progressValue = intPercentage
        binding.waterLevelViewG!!.centerTitle = "$intPercentage%"
        binding.waterLevelViewG!!.progressValue = intPercentage
        setBackGround()

        setTopChart()
        binding.textView60.text = requireContext().getString(R.string.intook_report) + " ("+
                sharedPref.getString(AppUtils.UNIT_STRING,"ml") + ")"
    }

    private fun setTopChart() {
        val model = ArrayList<BarChartModel>()
        val cursor: Cursor = sqliteHelper.getDailyIntookStats(date)

        if (cursor.moveToFirst()) {

            for (i in 0 until cursor.count) {
                model.add(BarChartModel(cursor.getInt(2).toExtractIntookOption(unit),
                    cursor.getFloat(3)))
                cursor.moveToNext()
            }

            val entries = fromModelToListOf(model,unit)

            generateTopChart(entries)

            val layoutParams: ConstraintLayout.LayoutParams = binding.textView60.layoutParams as
                    ConstraintLayout.LayoutParams
            layoutParams.startToStart = binding.chartDaily.id
            binding.textView60.layoutParams = layoutParams
            binding.chartDaily.visibility = View.VISIBLE
            binding.noData.visibility = View.GONE
        }
        else{
            val layoutParams: ConstraintLayout.LayoutParams = binding.textView60.layoutParams as
                    ConstraintLayout.LayoutParams
            layoutParams.startToStart = binding.noData.id
            binding.textView60.layoutParams = layoutParams
            binding.chartDaily.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
        }

        binding.most!!.text = sqliteHelper.getMaxTodayIntookStats(date)
            .toExtractIntookOption(unit).ifEmpty {
                requireContext().getString(R.string.none)
            }

        binding.least!!.text = sqliteHelper.getMinTodayIntookStats(date)
            .toExtractIntookOption(unit).ifEmpty {
            requireContext().getString(R.string.none)
        }

        if(binding.most!!.text != requireContext().getString(R.string.none)){
            if(binding.least!!.text != requireContext().getString(R.string.none)){
                if(binding.most!!.text == binding.least!!.text){
                    binding.least!!.text = requireContext().getString(R.string.none)
                }
            }
        }

        when(themeInt){
            0->binding.chartDaily.barsColor = resources.getColor(R.color.colorSecondary)
            1->binding.chartDaily.barsColor = resources.getColor(R.color.darkGreen)
            2->binding.chartDaily.barsColor = resources.getColor(R.color.colorSecondaryW)
            3->binding.chartDaily.barsColor = resources.getColor(R.color.purple_500)
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
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBackGround() {
        when(themeInt){
            0->toLightTheme()
            1->toDarkTheme()
            2->toWaterTheme()
            3->toGrapeTheme()
        }
    }

    private fun toGrapeTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_g)
        binding.textView8.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.waterLevelViewL!!.visibility = View.GONE
        binding.waterLevelViewD!!.visibility = View.GONE
        binding.waterLevelViewW!!.visibility = View.GONE
        binding.waterLevelViewG!!.visibility = View.VISIBLE
        setLayout(binding.waterLevelViewG.id)
    }

    private fun toWaterTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_w)
        binding.textView8.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.waterLevelViewL!!.visibility = View.GONE
        binding.waterLevelViewD!!.visibility = View.GONE
        binding.waterLevelViewW!!.visibility = View.VISIBLE
        binding.waterLevelViewG!!.visibility = View.GONE
        setLayout(binding.waterLevelViewW.id)
    }
    private fun toDarkTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg_dark)
        binding.textView8.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.waterLevelViewL!!.visibility = View.GONE
        binding.waterLevelViewD!!.visibility = View.VISIBLE
        binding.waterLevelViewW!!.visibility = View.GONE
        binding.waterLevelViewG!!.visibility = View.GONE
        setLayout(binding.waterLevelViewD.id)
    }
    private fun toLightTheme() {
        binding.layout.background = requireContext().getDrawable(R.drawable.ic_app_bg)
        binding.textView8.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.waterLevelViewL!!.visibility = View.VISIBLE
        binding.waterLevelViewD!!.visibility = View.GONE
        binding.waterLevelViewW!!.visibility = View.GONE
        binding.waterLevelViewG!!.visibility = View.GONE
        setLayout(binding.waterLevelViewL.id)
    }

    private fun setLayout(id: Int) {
        val layoutParams: ConstraintLayout.LayoutParams = binding.textView6.layoutParams as
                ConstraintLayout.LayoutParams
        layoutParams.startToStart = id
        layoutParams.topToTop = id
        binding.textView6.layoutParams = layoutParams
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {}
    override fun onNothingSelected() {}
    companion object {
        const val animationDuration = 1000L
    }
}