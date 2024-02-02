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
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager


class IntookCounterFragment :
    BaseFragment<IntookCounterStatsBottomSheetFragmentBinding>
        (IntookCounterStatsBottomSheetFragmentBinding::inflate),OnChartValueSelectedListener {


    private var themeInt : Int = 0
    private var unit : Int = 0
    private lateinit var sqliteHelper: SqliteHelper
    private var date : String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        date = SharedPreferencesManager.date
        unit = SharedPreferencesManager.current_unitInt
        themeInt = SharedPreferencesManager.themeInt
        super.onViewCreated(view, savedInstanceState)
        setBackGround()

        sqliteHelper = SqliteHelper(requireContext())
        binding.chartDaily.animate(listOf())

        val remaining = sqliteHelper.getTotalIntakeValue(date) - sqliteHelper.getIntook(date)

        if (remaining > 0) {
            binding.remainingIntake!!.text = "$remaining " + SharedPreferencesManager.unitString
        } else {
            binding.remainingIntake!!.text = "0 " + SharedPreferencesManager.unitString
        }

        binding.targetIntake!!.text = "${sqliteHelper.getTotalIntakeValue(date)
        } " + SharedPreferencesManager.unitString

        val percentage = sqliteHelper.getIntook(date) * 100 / sqliteHelper.getTotalIntakeValue(date)
        val intPercentage = percentage.toInt()
        startanimation(intPercentage)
        setBackGround()

        setTopChart()
        binding.textView60.text = requireContext().getString(R.string.intook_report) + " ("+
                SharedPreferencesManager.unitString + ")"
    }

    private fun startanimation(intPercentage: Int) {
        binding.waterLevelViewL!!.centerTitle = "$intPercentage%"
        binding.waterLevelViewL!!.progressValue = intPercentage
        binding.waterLevelViewD!!.centerTitle = "$intPercentage%"
        binding.waterLevelViewD!!.progressValue = intPercentage
        binding.waterLevelViewW!!.centerTitle = "$intPercentage%"
        binding.waterLevelViewW!!.progressValue = intPercentage
        binding.waterLevelViewG!!.centerTitle = "$intPercentage%"
        binding.waterLevelViewG!!.progressValue = intPercentage
        binding.waterLevelViewL!!.setAnimDuration(3000)
        binding.waterLevelViewD!!.setAnimDuration(3000)
        binding.waterLevelViewW!!.setAnimDuration(3000)
        binding.waterLevelViewG!!.setAnimDuration(3000)
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

        var textMax = requireContext().getString(R.string.none)

        var list = sqliteHelper.getMaxTodayIntookStats(date)

        if(list.isNotEmpty()){
            if(list.size==1){
                textMax = list[0].intook.toExtractIntookOption(unit)
            }
            else{
                var isMulti = false
                var max = 0
                for(i in list.indices){
                    if(i==0){
                        max = list[i].intook
                    }
                    else{
                        if(list[i].count.toInt() > list[i-1].count.toInt()){
                            max = list[i].intook
                            isMulti = false
                        }
                        else if((list[i].count.toInt() == list[i-1].count.toInt()) &&
                            max == list[i-1].intook) {
                            isMulti = true
                            max = -1
                        }
                    }
                }
                if(!isMulti){
                    textMax = max.toExtractIntookOption(unit)
                }
                else{
                    textMax = (-1).toExtractIntookOption(unit)
                }
            }
        }

        binding.most!!.text = textMax

        var textMin = requireContext().getString(R.string.none)

        var listMin = sqliteHelper.getMinTodayIntookStats(date)

        if(listMin.isNotEmpty()){
            if(listMin.size==1){
                textMin = listMin[0].intook.toExtractIntookOption(unit)
            }
            else{
                var isMulti = false
                var min = 0
                for(i in listMin.indices){
                    if(i==0){
                        min = listMin[i].intook
                    }
                    else{
                        if(listMin[i].count.toInt() < listMin[i-1].count.toInt()){
                            min = listMin[i].intook
                            isMulti = false
                        }
                        else if((listMin[i].count.toInt() == listMin[i-1].count.toInt()) &&
                            min == listMin[i-1].intook) {
                            isMulti = true
                            min = -1
                        }
                    }
                }
                if(!isMulti){
                    textMin = min.toExtractIntookOption(unit)
                }
                else{
                    textMin = (-1).toExtractIntookOption(unit)
                }
            }
        }

        binding.least!!.text = textMin

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
            7.toExtractIntookOption(unit) to (model.singleOrNull { s ->
                s.type == 7.toExtractIntookOption(
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