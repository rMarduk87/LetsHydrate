package rpt.tool.mementobibere.ui.statistics.stats

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.Button
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.textfield.TextInputLayout
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.data.models.MonthChartModel
import rpt.tool.mementobibere.databinding.StatsFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.chart.ChartXValueFormatter
import rpt.tool.mementobibere.utils.extensions.toCalculatedValueStats
import rpt.tool.mementobibere.utils.extensions.toExtractFloat
import rpt.tool.mementobibere.utils.extensions.toExtractIntookOption
import rpt.tool.mementobibere.utils.extensions.toMonth
import rpt.tool.mementobibere.utils.extensions.toNumberString
import rpt.tool.mementobibere.utils.extensions.toStats
import rpt.tool.mementobibere.utils.extensions.toYear
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.max

class StatsFragment : BaseFragment<StatsFragmentBinding>(StatsFragmentBinding::inflate),
    CompoundButton.OnCheckedChangeListener {

    private lateinit var sharedPref: SharedPreferences
    private var themeInt : Int = 0
    private var isMonth : Boolean = true
    private var indexMonth : Int = 0
    private var indexYear : Int = 0
    private lateinit var listOfMonths : List<MonthChartModel>
    private lateinit var listOfYears : List<String>
    private lateinit var listOfWeeks : List<String>
    private lateinit var sqliteHelper: SqliteHelper
    private var unit : Int = 0
    private var weekAverage : Float = 0f
    private var monthAverage : Float = 0f
    private var completion : Float = 0f
    private var drinkFrequency : Float = 0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        unit = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        setBackGround()
        if(sharedPref.getBoolean(AppUtils.SEE_TIPS_KEY,true)){
            binding.bubble.visibility = View.VISIBLE
            binding.se.visibility = View.VISIBLE
        }
        else{
            binding.bubble.visibility = View.VISIBLE
            binding.se.visibility = View.VISIBLE
        }

        binding.monthSwitch.setOnCheckedChangeListener(this)


        listOfWeeks = AppUtils.getWeekList(AppUtils.getCurrentDate()!!)

        listOfMonths = AppUtils.
        getDateList(AppUtils.START_DATE,AppUtils.getCurrentDate()!!,"MMM yyyy")!!
        listOfYears = AppUtils.
        getDateListForYear(AppUtils.START_DATE,AppUtils.getCurrentDate()!!,"yyyy")!!
        isMonth = sharedPref.getBoolean(AppUtils.STAT_IS_MONTH_KEY,true)
        indexMonth = sharedPref.getInt(AppUtils.INDEX_MONTH_KEY,listOfMonths.size -1)
        indexYear = sharedPref.getInt(AppUtils.INDEX_YEAR_KEY,listOfYears.size -1)

        if(isMonth){
            binding.monthOrYearTV.text = listOfMonths[indexMonth].text
            binding.monthSwitch.isChecked = true
        }
        else{
            binding.monthOrYearTV.text = listOfYears[indexYear]
            binding.monthSwitch.isChecked = false
        }
        if(indexMonth == listOfMonths.size - 1 && isMonth){
            binding.forward.isEnabled = false
        }
        if(indexYear == listOfYears.size - 1 && !isMonth){
            binding.forward.isEnabled = false
        }
        if(indexMonth == 0 && isMonth){
            binding.backward.isEnabled = false
        }
        if(indexMonth == 0 && !isMonth){
            binding.backward.isEnabled = false
        }

        binding.forward.setOnClickListener{
            when(isMonth){
                true->setForwardMonth()
                false->setForwardYear()
            }
        }

        binding.backward.setOnClickListener{
            when(isMonth){
                true->setBackwardMonth()
                false->setBackwardYear()
            }
        }

        createChart()
        createWeekStats()

        binding.sun.setOnClickListener{
            putDate(listOfWeeks[0])
            safeNavController?.safeNavigate(
                StatsFragmentDirections.
                actionStatsFragmentToIntookCounterBottomSheetDialog())
        }
        binding.mon.setOnClickListener{
            putDate(listOfWeeks[1])
            safeNavController?.safeNavigate(
                StatsFragmentDirections
                    .actionStatsFragmentToIntookCounterBottomSheetDialog())
        }
        binding.tue.setOnClickListener{
            putDate(listOfWeeks[2])
            safeNavController?.safeNavigate(
                StatsFragmentDirections
                    .actionStatsFragmentToIntookCounterBottomSheetDialog())
        }
        binding.wed.setOnClickListener{
            putDate(listOfWeeks[3])
            safeNavController?.safeNavigate(
                StatsFragmentDirections
                    .actionStatsFragmentToIntookCounterBottomSheetDialog())
        }
        binding.thu.setOnClickListener{
            putDate(listOfWeeks[4])
            safeNavController?.safeNavigate(
                StatsFragmentDirections
                    .actionStatsFragmentToIntookCounterBottomSheetDialog())
        }
        binding.fri.setOnClickListener{
            putDate(listOfWeeks[5])
            safeNavController?.safeNavigate(
                StatsFragmentDirections
                    .actionStatsFragmentToIntookCounterBottomSheetDialog())
        }
        binding.sat.setOnClickListener{
            putDate(listOfWeeks[6])
            safeNavController?.safeNavigate(
                StatsFragmentDirections
                    .actionStatsFragmentToIntookCounterBottomSheetDialog())
        }

        binding.btnAddRecord.setOnClickListener {
            val li = LayoutInflater.from(requireContext())
            val promptsView = li.inflate(R.layout.add_reached_custom_input_dialog, null)

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(promptsView)

            val userInput = promptsView
                .findViewById(R.id.etReachedDay) as TextInputLayout

            val userIputQta = promptsView.findViewById(R.id.etReachedQta) as TextInputLayout

            userInput.editText!!.setOnClickListener {
                val calendar = Calendar.getInstance()

                val mDatePicker = DatePickerDialog(
                    requireContext(),
                    { _, year, monthOfYear, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, monthOfYear)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        val myFormat = "dd-MM-yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat)
                        userInput.editText!!.setText(sdf.format(calendar.time))
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                mDatePicker.datePicker.maxDate = AppUtils.getMaxDate()
                mDatePicker.setTitle("")
                mDatePicker.show()
            }

            val userBtnAdd = promptsView.findViewById(R.id.btnAdd) as Button

            val sqliteHelper = SqliteHelper(requireContext())

            val unit = AppUtils.calculateExtensions(sharedPref.getInt(AppUtils.UNIT_KEY, 0))

            var value = 0f
            if(!TextUtils.isEmpty(userIputQta.editText!!.text.toString())){
                value = userIputQta.editText!!.text.toString().toFloat()
            }

            userBtnAdd.setOnClickListener {
                when {

                    TextUtils.isEmpty(userInput.editText!!.text.toString()) ->
                        showError(getString(R.string.please_input_a_valid_date))

                    value != 0f && value < sharedPref.getFloat(
                        AppUtils.TOTAL_INTAKE_KEY, 0f
                    ) -> showError(
                        "${getString(R.string.please_enter_a_quantity_at_least_equal_to)} "
                                + sharedPref.getFloat(
                        AppUtils.TOTAL_INTAKE_KEY, 0f
                    ) + " " + AppUtils.calculateExtensions(
                            sharedPref.getInt(AppUtils.UNIT_KEY,0)))

                    else -> {

                        var qta =  if(value != 0f){
                            value
                        }else{
                            sharedPref.getFloat(
                                AppUtils.TOTAL_INTAKE_KEY, 0f
                            )
                        }

                        sqliteHelper.addReachedGoal(
                            userInput.editText!!.text.toString(), qta, unit
                        )
                        sqliteHelper.addAll(
                            userInput.editText!!.text.toString(),
                            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f),
                            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f),unit,
                            userInput.editText!!.text.toString().toMonth(),
                            userInput.editText!!.text.toString().toYear()
                        )
                        sqliteHelper.addOrUpdateIntookCounter(
                            userInput.editText!!.text.toString(),6f,1)
                        userInput.editText!!.setText("")
                        userIputQta.editText!!.setText("")
                    }
                }
            }

            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                createChart()
                createWeekStats()
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        binding.btnShowRecord.setOnClickListener {
            safeNavController?.safeNavigate(
                StatsFragmentDirections.actionStatsFragmentToReacheGoalFragment())
        }
    }

    private fun createWeekStats() {
        binding.sun.setAnimation(assignIconByIntook(listOfWeeks[0]))
        binding.mon.setAnimation(assignIconByIntook(listOfWeeks[1]))
        binding.tue.setAnimation(assignIconByIntook(listOfWeeks[2]))
        binding.wed.setAnimation(assignIconByIntook(listOfWeeks[3]))
        binding.thu.setAnimation(assignIconByIntook(listOfWeeks[4]))
        binding.fri.setAnimation(assignIconByIntook(listOfWeeks[5]))
        binding.sat.setAnimation(assignIconByIntook(listOfWeeks[6]))

        binding.sun.playAnimation()
        binding.mon.playAnimation()
        binding.tue.playAnimation()
        binding.wed.playAnimation()
        binding.thu.playAnimation()
        binding.fri.playAnimation()
        binding.sat.playAnimation()

        var sumWeek = 0f
        for (i in listOfWeeks.indices) {
            sumWeek += sqliteHelper.getIntook(listOfWeeks[i])
        }

        weekAverage = sumWeek/7
        binding.weekAverageTV.text = "${weekAverage.toNumberString()} ${
            AppUtils.calculateExtensions(
                sharedPref.getInt(
                    AppUtils.UNIT_KEY,
                    0
                )
            )
        } / ${requireContext().getString(R.string.day)}"

        var sumMonth = 0f
        var cursor = sqliteHelper.getAllStatsMonthly(AppUtils.getCurrentDate()!!.toMonth(),
            AppUtils.getCurrentDate()!!.toYear())
        if (cursor.moveToFirst()) {

            for (i in 0 until cursor.count) {
                sumMonth += cursor.getFloat(7)
                cursor.moveToNext()
            }
        }
        monthAverage = sumMonth/AppUtils.getTotalDays(AppUtils.getCurrentDate()!!.toMonth(),
            AppUtils.getCurrentDate()!!.toYear())
        binding.monthAverageTV.text = "${monthAverage.toNumberString()} ${
            AppUtils.calculateExtensions(
                sharedPref.getInt(
                    AppUtils.UNIT_KEY,
                    0
                )
            )
        } / ${requireContext().getString(R.string.day)}"

        var percentage = 0f

        for (i in listOfWeeks.indices) {
            var drinkedD = sqliteHelper.getIntook(listOfWeeks[i])
            var toCompleteD = sqliteHelper.getTotalIntakeValue(listOfWeeks[i])
            if(toCompleteD != 0f){
                percentage +=  drinkedD * 100/toCompleteD
            }
        }

        completion = percentage /7
        binding.reachedAverageTV.text = "${completion.toNumberString()} %"

        var sumCount = 0f
        for (i in listOfWeeks.indices) {
            var c = sqliteHelper.getSumOfDailyIntookCounter(listOfWeeks[i])
            c.use {
                if (it.moveToFirst()) {
                    sumCount += it.getInt(1)
                }
            }
        }

        drinkFrequency = (sumCount/7).toString().toExtractFloat().toStats()
        binding.drinkFrequencyTV.text = "${drinkFrequency.toNumberString()} ${
            requireContext().getString(R.string.times)
        } / ${requireContext().getString(R.string.day)}"
    }

    private fun assignIconByIntook(date: String): Int {
        var drinked = sqliteHelper.getIntook(date)
        var totalIntake = 0f
        sqliteHelper.getTotalIntake(date).use {
            if (it.moveToFirst()) {
                totalIntake = it.getFloat(1)
            }
        }

        if(drinked >= totalIntake && totalIntake > 0f){
            return R.raw.trophy
        }

        if(totalIntake == 0f){
            return R.raw.no_data_drink
        }

        return when(themeInt){
            0-> R.raw.cry
            1-> R.raw.cry_d
            2-> R.raw.cry_w
            3-> R.raw.cry_g
            else -> R.raw.cry
        }
    }

    private fun putDate(date: String) {
        val editor = sharedPref.edit()
        editor.putString(AppUtils.DATE, date)
        editor.apply()
    }

    private fun setForwardMonth() {
        if(indexMonth < listOfMonths.size - 1){
            indexMonth += 1
            binding.monthOrYearTV.text = listOfMonths[indexMonth].text
            binding.backward.isEnabled = true
            if(indexMonth == listOfMonths.size - 1){
                binding.forward.isEnabled = false
            }
            createChart()
        }
    }

    private fun setForwardYear() {
        if(indexYear < listOfYears.size - 1){
            indexYear += 1
            binding.monthOrYearTV.text = listOfYears[indexYear]
            binding.backward.isEnabled = true
            if(indexYear == listOfYears.size - 1){
                binding.forward.isEnabled = false
            }
            createChart()
        }
    }

    private fun setBackwardMonth() {
        if(indexMonth > 0){
            indexMonth -= 1
            binding.monthOrYearTV.text = listOfMonths[indexMonth].text
            binding.forward.isEnabled = true
            if(indexMonth == 0){
                binding.backward.isEnabled = false
            }
        }
        createChart()
    }

    private fun setBackwardYear() {
        if(indexYear > 0){
            indexYear -= 1
            binding.monthOrYearTV.text = listOfYears[indexYear]
            binding.forward.isEnabled = true
            if(indexYear == 0){
                binding.backward.isEnabled = false
            }
        }
        createChart()
    }

    private fun createChart() {
        sqliteHelper = SqliteHelper(requireContext())

        val entries = ArrayList<Entry>()
        val dateArray = ArrayList<String>()

        val cursor: Cursor = if(isMonth){
            sqliteHelper.getAllStatsMonthly(listOfMonths[indexMonth].month.toMonth(),
                listOfMonths[indexMonth].month.toYear())
        }
        else{
            sqliteHelper.getAllStatsYear(listOfYears[indexYear])
        }

        if (cursor.moveToFirst()) {

            for (i in 0 until cursor.count) {
                dateArray.add(cursor.getString(1))
                val percent = cursor.getFloat(7) /
                        cursor.getFloat(8) * 100
                entries.add(
                    Entry(
                        i.toFloat(),
                        percent
                    )
                )
                cursor.moveToNext()
            }

        } else {
            val layoutParams: LayoutParams = binding.df.layoutParams as LayoutParams
            layoutParams.topToBottom = binding.noData.id
            binding.df.layoutParams = layoutParams
            binding.chart.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
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
                binding.chart.axisLeft.textColor = setThemeColor()
                binding.chart.xAxis.textColor = setThemeColor()
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
                val layoutParams: LayoutParams = binding.df.layoutParams as LayoutParams
                layoutParams.topToBottom = binding.chart.id
                binding.df.layoutParams = layoutParams
                binding.chart.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
            }
            else{
                val layoutParams: LayoutParams = binding.df.layoutParams as LayoutParams
                layoutParams.topToBottom = binding.noData.id
                binding.df.layoutParams = layoutParams
                binding.chart.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
            }
            setBackGround()
        }
    }

    private fun setThemeColor(): Int {
        when(themeInt){
            3-> return Color.parseColor("#FF6200EE")
        }
        return Color.BLACK
    }

    private fun setColor(themeInt: Int): Int {
        when(themeInt){
            0-> return R.color.colorSecondaryDark
            1-> return R.color.darkGreen
            2-> return R.color.colorSecondaryDarkW
            3-> return R.color.purple_500
        }
        return R.color.colorSecondaryDark
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setDrawable(): Drawable? {
        when(themeInt){
            0-> return requireContext().getDrawable(R.drawable.graph_fill_gradiant)
            1-> return requireContext().getDrawable(R.drawable.graph_fill_gradiant_dark)
            2-> return requireContext().getDrawable(R.drawable.graph_fill_gradiant_water)
            3-> return requireContext().getDrawable(R.drawable.graph_fill_gradiant_grape)
        }
        return requireContext().getDrawable(R.drawable.graph_fill_gradiant)
    }

    private fun setBackGround() {
        when(themeInt){
            0->toLightTheme()
            1->toDarkTheme()
            2->toWaterTheme()
            3->toGrapeTheme()
        }
    }

    private fun toGrapeTheme() {
        binding.weeklyView.setBackgroundColor(requireContext().getColor(R.color.purple_500))
        binding.layout.setBackgroundColor(requireContext().getColor(R.color.colorBlack))
        binding.weekAverageTV.setTextColor(requireContext().getColor(R.color.purple_500))
        binding.monthAverageTV.setTextColor(requireContext().getColor(R.color.purple_500))
        binding.reachedAverageTV.setTextColor(requireContext().getColor(R.color.purple_500))
        binding.drinkFrequencyTV.setTextColor(requireContext().getColor(R.color.purple_500))
        binding.reports.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.repWeekTV.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.repMonthTV.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.repReachedTV.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.repDrinkFreqTV.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.btnShowRecord.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.btnAddRecord.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.monthOrYearTV.setTextColor(requireContext().getColor(R.color.purple_500))
        binding.monthLabel.setTextColor(requireContext().getColor(R.color.purple_500))
        binding.bubble.setImageDrawable(requireContext().getDrawable(R.drawable.ic_bubbles_g))
    }

    private fun toWaterTheme() {
        binding.weeklyView.setBackgroundColor(requireContext().getColor(R.color.colorSecondaryDarkW))
        binding.layout.setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.weekAverageTV.setTextColor(requireContext().getColor(R.color.colorSecondaryDarkW))
        binding.monthAverageTV.setTextColor(requireContext().getColor(R.color.colorSecondaryDarkW))
        binding.reachedAverageTV.setTextColor(requireContext().getColor(R.color.colorSecondaryDarkW))
        binding.drinkFrequencyTV.setTextColor(requireContext().getColor(R.color.colorSecondaryDarkW))
        setOtherLayout()
    }

    private fun toDarkTheme() {
        binding.weeklyView.setBackgroundColor(requireContext().getColor(R.color.darkGreen30))
        binding.layout.setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.weekAverageTV.setTextColor(requireContext().getColor(R.color.darkGreen30))
        binding.monthAverageTV.setTextColor(requireContext().getColor(R.color.darkGreen30))
        binding.reachedAverageTV.setTextColor(requireContext().getColor(R.color.darkGreen30))
        binding.drinkFrequencyTV.setTextColor(requireContext().getColor(R.color.darkGreen30))
        setOtherLayout()
    }

    private fun toLightTheme() {
        binding.weeklyView.setBackgroundColor(requireContext().getColor(R.color.colorSecondaryDark))
        binding.layout.setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.weekAverageTV.setTextColor(requireContext().getColor(R.color.colorSecondaryDark))
        binding.monthAverageTV.setTextColor(requireContext().getColor(R.color.colorSecondaryDark))
        binding.reachedAverageTV.setTextColor(requireContext().getColor(R.color.colorSecondaryDark))
        binding.drinkFrequencyTV.setTextColor(requireContext().getColor(R.color.colorSecondaryDark))
        setOtherLayout()
    }

    private fun setOtherLayout() {
        binding.reports.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.repWeekTV.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.repMonthTV.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.repReachedTV.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.repDrinkFreqTV.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.btnShowRecord.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.btnAddRecord.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.monthOrYearTV.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.monthLabel.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.bubble.setImageDrawable(requireContext().getDrawable(R.drawable.ic_bubbles))
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, checked: Boolean) {
        var text = if(checked){
            requireContext().getString(R.string.month)
        }else{
            requireContext().getString(R.string.year)
        }

        isMonth = checked

        val editor = sharedPref.edit()
        editor.putInt(AppUtils.INDEX_MONTH_KEY,indexMonth)
        editor.putInt(AppUtils.INDEX_YEAR_KEY,indexYear)
        editor.putBoolean(AppUtils.STAT_IS_MONTH_KEY,isMonth)
        editor.apply()

        binding.monthLabel.text = text
        if(isMonth){
            indexMonth = sharedPref.getInt(AppUtils.INDEX_MONTH_KEY,listOfMonths.size -1)
            binding.monthOrYearTV.text = listOfMonths[indexMonth].text
            //binding.monthSwitch.isChecked = !checked
        }
        else{
            indexYear = sharedPref.getInt(AppUtils.INDEX_YEAR_KEY,listOfYears.size -1)
            binding.monthOrYearTV.text = listOfYears[indexYear]
            //binding.monthSwitch.isChecked = checked
        }
        createChart()
    }
}