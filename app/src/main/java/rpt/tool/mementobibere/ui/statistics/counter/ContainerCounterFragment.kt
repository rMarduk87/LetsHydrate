package rpt.tool.mementobibere.ui.statistics.counter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.data.appmodel.BarChartModel
import rpt.tool.mementobibere.databinding.FragmentContainerCountBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.extensions.toCalendar
import rpt.tool.mementobibere.utils.extensions.toExtractIntookOption
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.helpers.StringHelper
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.log.e
import java.util.Calendar
import java.util.Locale

class ContainerCounterFragment :
    BaseFragment<FragmentContainerCountBinding>(FragmentContainerCountBinding::inflate) {

    private var maxCount: Int = 0
    var current_calendar: Calendar? = null
    var start_calendarN: Calendar? = null
    var sqliteHelper: SqliteHelper? = null
    var stringHelper: StringHelper? = null
    var lst_opt: MutableList<String> = ArrayList()
    var lst_opt_OZ: MutableList<String> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lst_opt.clear()
        lst_opt_OZ.clear()

        lst_opt.add("50")
        lst_opt.add("100")
        lst_opt.add("150")
        lst_opt.add("200")
        lst_opt.add("250")
        lst_opt.add("300")
        lst_opt.add("500")
        lst_opt.add("600")
        lst_opt.add("700")
        lst_opt.add("800")
        lst_opt.add("900")
        lst_opt.add("1000")
        lst_opt.add("Custom")

        lst_opt_OZ.add("1.69")
        lst_opt_OZ.add("3.38")
        lst_opt_OZ.add("5.07")
        lst_opt_OZ.add("6.76")
        lst_opt_OZ.add("8.45")
        lst_opt_OZ.add("10.14")
        lst_opt_OZ.add("16.91")
        lst_opt_OZ.add("20.89")
        lst_opt_OZ.add("23.67")
        lst_opt_OZ.add("27.05")
        lst_opt_OZ.add("30.43")
        lst_opt_OZ.add("33.81")
        lst_opt_OZ.add("Custom")

        setCurrentDayInfo()
        body()
    }

    private fun setCurrentDayInfo() {
        current_calendar = Calendar.getInstance(Locale.getDefault())
    }

    @SuppressLint("SetTextI18n")
    private fun body() {

        stringHelper = StringHelper(requireContext(),requireActivity())
        sqliteHelper = SqliteHelper(requireContext())

        binding.include1.lblToolbarTitle.text = requireContext().getString(R.string.str_counter)
        binding.include1.leftIconBlock.setOnClickListener { finish() }
        binding.include1.rightIconBlock.visibility = View.GONE

        start_calendarN = Calendar.getInstance(Locale.getDefault())

        start_calendarN!!.set(Calendar.HOUR_OF_DAY, 0)
        start_calendarN!!.set(Calendar.MINUTE, 0)
        start_calendarN!!.set(Calendar.SECOND, 0)
        start_calendarN!!.set(Calendar.MILLISECOND, 0)


        binding.include2.imgPre.setOnClickListener {
            start_calendarN!!.add(Calendar.DAY_OF_MONTH, -1)

            generateBarDataNew()
        }

        binding.include2.imgNext.setOnClickListener(View.OnClickListener {
            start_calendarN!!.add(Calendar.DAY_OF_MONTH, 1)

            d(
                "MIN_MAX_DATE 2.1 : ", "" +
                        start_calendarN!!.timeInMillis
            )


            if (start_calendarN!!.timeInMillis > AppUtils.getCurrentDate()!!.toCalendar().timeInMillis) {
                start_calendarN!!.add(Calendar.DAY_OF_MONTH, -1)
                return@OnClickListener
            }

            generateBarDataNew()
        })


        generateBarDataNew()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun generateBarDataNew() {
        val model = ArrayList<BarChartModel>()
        val start_calendar = Calendar.getInstance(Locale.getDefault())
        start_calendar.timeInMillis = start_calendarN!!.timeInMillis

        binding.include2.lblTitle.text = (AppUtils.getDate(start_calendar.timeInMillis,
            AppUtils.DATE_FORMAT))

        val date = (("" + stringHelper!!.get_2_point("" +
                start_calendar[Calendar.DAY_OF_MONTH])) + "-" + stringHelper!!.get_2_point(
            "" + (start_calendar[Calendar.MONTH] + 1)
        )) + "-" + start_calendar[Calendar.YEAR]

        val arr_data = sqliteHelper!!.getdata("intook_count", "date ='" +
                 date + "'")

        for(j in lst_opt.indices){
            val data = arr_data.map { it["intook"]!!.toInt().toExtractIntookOption("ml") }.toList()
            if(data.contains(lst_opt[j])){
                val option = data[data.indexOf(lst_opt[j])]
                val counter = arr_data[data.indexOf(lst_opt[j])]["intook_count"]!!.toFloat()
                model.add(BarChartModel(option,counter))
            }
            else{
                model.add(BarChartModel(lst_opt[j],0f))
            }
        }


        maxCount = model.map{it.counter}.toList().maxOfOrNull { it }!!.toInt()

        generateTopChart(model)

        var textMax = requireContext().getString(R.string.none)

        val list = sqliteHelper!!.getMaxTodayIntookStats(date)

        if(list.isNotEmpty()){
            if(list.size==1){
                textMax = list[0].intook.toExtractIntookOption(AppUtils.WATER_UNIT_VALUE)
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

                val text = if(max != -1) {
                    if (AppUtils.WATER_UNIT_VALUE.equals("ml", true)) {
                        max.toExtractIntookOption(AppUtils.WATER_UNIT_VALUE) + " ml"
                    } else {
                        AppUtils.mlToOzUS(
                            max.toExtractIntookOption(AppUtils.WATER_UNIT_VALUE)
                                .toFloat()
                        ).toString() + " fl oz"
                    }
                } else {
                    ""
                }

                textMax = if(!isMulti) {
                    text
                }
                else{
                    (-1).toExtractIntookOption(AppUtils.WATER_UNIT_VALUE)
                }
            }
        }

        binding.include2.txtOptSelected.text = textMax

        var textMin = requireContext().getString(R.string.none)

        val listMin = sqliteHelper!!.getMinTodayIntookStats(date)

        if(listMin.isNotEmpty()){
            if(listMin.size==1){
                textMin = listMin[0].intook.toExtractIntookOption(AppUtils.WATER_UNIT_VALUE)
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

                val text = if(min!=-1){if(AppUtils.WATER_UNIT_VALUE.equals("ml",true)){
                    min.toExtractIntookOption(AppUtils.WATER_UNIT_VALUE) + " ml"
                } else{
                    AppUtils.mlToOzUS(min.toExtractIntookOption(AppUtils.WATER_UNIT_VALUE)
                        .toFloat()).toString() + " fl oz"
                }} else {
                    ""
                }


                textMin = if(!isMulti){
                    text
                } else{
                    (-1).toExtractIntookOption(AppUtils.WATER_UNIT_VALUE)
                }
            }
        }

        binding.include2.txtLessSelected.text = textMin

        if(binding.include2.txtOptSelected.text != requireContext().getString(R.string.none)){
            if(binding.include2.txtLessSelected.text != requireContext().getString(R.string.none)){
                if(binding.include2.txtOptSelected.text == binding.include2.txtLessSelected.text){
                    binding.include2.txtLessSelected.text = requireContext().getString(R.string.none)
                }
            }
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun generateTopChart(barBottomSet: ArrayList<BarChartModel>) {

        binding.include2.chart1.clear()

        binding.include2.chart1.description.isEnabled = false

        // if more than 60 entries are displayed in the binding.include2.chart1, no values will be
        // drawn
        binding.include2.chart1.setMaxVisibleValueCount(40)

        binding.include2.chart1.setDrawGridBackground(false)
        binding.include2.chart1.setDrawBarShadow(false)

        binding.include2.chart1.setDrawValueAboveBar(false)
        binding.include2.chart1.isHighlightFullBarEnabled = false

        val leftAxis: YAxis = binding.include2.chart1.axisLeft
        leftAxis.textColor = requireContext().resources.getColor(R.color.rdo_gender_select)


        leftAxis.setAxisMaximum(maxBarGraphVal)

        leftAxis.setAxisMinimum(0f) // this replaces setStartAtZero(true)
        binding.include2.chart1.axisRight.isEnabled = false

        binding.include2.chart1.extraBottomOffset = 20f

        val xLabels: XAxis = binding.include2.chart1.xAxis
        xLabels.setDrawGridLines(false)
        xLabels.isGranularityEnabled = false
        xLabels.position = XAxis.XAxisPosition.BOTTOM
        xLabels.textColor = requireContext().resources.getColor(R.color.rdo_gender_select)
        xLabels.setLabelCount(12)

        val xAxisFormatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                try {
                    return if(AppUtils.WATER_UNIT_VALUE.equals("ml",true)){
                        lst_opt[value.toInt()]
                    } else{
                        lst_opt_OZ[value.toInt()]
                    }

                } catch (e: Exception) {
                    e.message?.let { e(Throwable(e), it) }
                }
                return "N/A"
            }
        }

        xLabels.valueFormatter = xAxisFormatter

        val l: Legend = binding.include2.chart1.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.formSize = 8f
        l.formToTextSpace = 4f
        l.xEntrySpace = 6f
        l.isEnabled = false

        val values: ArrayList<BarEntry> = ArrayList<BarEntry>()

        for (i in barBottomSet.indices) {
            val val1 = barBottomSet[i].counter


            d("========", "$val1")

            values.add(
                BarEntry(
                    i.toFloat(),
                    val1,
                    resources.getDrawable(R.drawable.ic_launcher_background)
                )
            )
        }

        val set1: BarDataSet

        if (binding.include2.chart1.data != null &&
            binding.include2.chart1.data.getDataSetCount() > 0
        ) {
            set1 = binding.include2.chart1.data.getDataSetByIndex(0) as BarDataSet
            set1.setValues(values)
            set1.highLightAlpha = 50
            set1.setDrawValues(false)
            binding.include2.chart1.data.notifyDataChanged()
            binding.include2.chart1.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "")
            set1.setDrawIcons(false)
            set1.setColors(*colors)
            set1.highLightAlpha = 50

            val dataSets: ArrayList<IBarDataSet> = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data: BarData = BarData(dataSets)

            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    try {
                        for (k in barBottomSet.indices) {
                            return "" + barBottomSet[k].counter
                        }
                    } catch (e: Exception) {
                        e.message?.let { e(Throwable(e), it) }
                    }

                    return "" + value.toInt()
                }
            })

            data.setValueTextColor(requireContext().resources.getColor(R.color.btn_back))
            set1.setDrawValues(false)
            set1.valueTextSize = 10f
            binding.include2.chart1.setData(data)
        }

        binding.include2.chart1.animateY(1500)

        binding.include2.chart1.setPinchZoom(false)
        binding.include2.chart1.setScaleEnabled(false)

        binding.include2.chart1.invalidate()

    }

    private val maxBarGraphVal: Float
        get() {
            return ((((maxCount / 10)) + 1) * 10).toFloat()
        }

    private val colors: IntArray
        get() {
            // have as many colors as stack-values per entry

            val colors = IntArray(1)
            colors[0] = requireContext().resources.getColor(R.color.rdo_gender_select)

            return colors
        }

    private fun finish() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
    }

}