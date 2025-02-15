package rpt.tool.mementobibere.ui.statistics.stats

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.view.Window
import android.widget.ImageView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.Utils
import com.google.gson.Gson
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.databinding.FragmentStatsYearBinding
import java.util.Calendar
import java.util.Locale
import java.util.Random
import android.annotation.SuppressLint
import android.os.Bundle
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.helpers.StringHelper
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import android.graphics.DashPathEffect
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.WindowManager.LayoutParams.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import rpt.tool.mementobibere.utils.log.i

class YearStatsFragment : BaseFragment<FragmentStatsYearBinding>(FragmentStatsYearBinding::inflate) {

    var lst_date: MutableList<String> = ArrayList()
    var lst_date2: MutableList<String> = ArrayList()
    var lst_date_val: MutableList<Int> = ArrayList()
    var lst_month: ArrayList<String> = ArrayList()
    var lst_month_display: ArrayList<String> = ArrayList()
    var lst_date_goal_val: MutableList<Int> = ArrayList()
    var lst_date_goal_val_2: MutableList<Int> = ArrayList()
    var current_start_calendar: Calendar? = null
    var current_end_calendar: Calendar? = null
    var start_calendarN: Calendar? = null
    var end_calendarN: Calendar? = null
    var stringHelper: StringHelper? = null
    var sqliteHelper: SqliteHelper? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCurrentYearInfo()

        body()
    }


    private fun setCurrentYearInfo() {
        current_start_calendar = Calendar.getInstance(Locale.getDefault())
        current_start_calendar!!.set(Calendar.DAY_OF_YEAR, 1)
        current_start_calendar!!.set(Calendar.HOUR_OF_DAY, 0)
        current_start_calendar!!.set(Calendar.MINUTE, 0)
        current_start_calendar!!.set(Calendar.SECOND, 0)
        current_start_calendar!!.set(Calendar.MILLISECOND, 0)

        current_end_calendar = Calendar.getInstance(Locale.getDefault())
        current_end_calendar!!.set(Calendar.MONTH, 11)
        current_end_calendar!!.set(Calendar.DAY_OF_MONTH, 31)
        current_end_calendar!!.set(Calendar.HOUR_OF_DAY, 23)
        current_end_calendar!!.set(Calendar.MINUTE, 59)
        current_end_calendar!!.set(Calendar.SECOND, 59)
        current_end_calendar!!.set(Calendar.MILLISECOND, 999)
    }

    private fun body() {

        stringHelper = StringHelper(requireContext(),requireActivity())
        sqliteHelper = SqliteHelper(requireContext())
        
        start_calendarN = Calendar.getInstance(Locale.getDefault())
        start_calendarN!!.set(Calendar.DAY_OF_YEAR, 1)
        start_calendarN!!.set(Calendar.HOUR_OF_DAY, 0)
        start_calendarN!!.set(Calendar.MINUTE, 0)
        start_calendarN!!.set(Calendar.SECOND, 0)
        start_calendarN!!.set(Calendar.MILLISECOND, 0)

        end_calendarN = Calendar.getInstance(Locale.getDefault())
        end_calendarN!!.set(Calendar.MONTH, 11)
        end_calendarN!!.set(Calendar.DAY_OF_MONTH, 31)
        end_calendarN!!.set(Calendar.HOUR_OF_DAY, 23)
        end_calendarN!!.set(Calendar.MINUTE, 59)
        end_calendarN!!.set(Calendar.SECOND, 59)
        end_calendarN!!.set(Calendar.MILLISECOND, 999)

        lst_month = stringHelper!!.get_arraylist(R.array.month_list2)
        lst_month_display = stringHelper!!.get_arraylist(R.array.month_list)

        loadData(start_calendarN!!, end_calendarN!!)

        binding.imgPre.setOnClickListener {
            start_calendarN!!.add(Calendar.YEAR, -1)
            end_calendarN!!.add(Calendar.YEAR, -1)
            loadData(start_calendarN!!, end_calendarN!!)
            generateBarDataNew()
        }
        
        binding.imgNext.setOnClickListener(View.OnClickListener {
            start_calendarN!!.add(Calendar.YEAR, 1)
            end_calendarN!!.add(Calendar.YEAR, 1)

            d(
                "MIN_MAX_DATE 2.1 : ", "" +
                        start_calendarN!!.timeInMillis +
                        " @@@ " + "" + end_calendarN!!.timeInMillis
            )


            if (start_calendarN!!.timeInMillis >= current_end_calendar!!.timeInMillis) {
                start_calendarN!!.add(Calendar.YEAR, -1)
                end_calendarN!!.add(Calendar.YEAR, -1)
                return@OnClickListener
            }

            loadData(start_calendarN!!, end_calendarN!!)
            generateBarDataNew()
        })


        generateDataNew()

        generateBarDataNew()
    }


    @SuppressLint("SetTextI18n")
    private fun loadData(start_calendar2: Calendar, end_calendar2: Calendar) {
        //lbl_title.setText(AppUtils.getCurrentDate("MMM yyyy"));

        val start_calendar = Calendar.getInstance(Locale.getDefault())
        start_calendar.timeInMillis = start_calendar2.timeInMillis
        val end_calendar = Calendar.getInstance(Locale.getDefault())
        end_calendar.timeInMillis = end_calendar2.timeInMillis

        binding.lblTitle.text = AppUtils.getDate(end_calendar.timeInMillis, "yyyy")

        lst_date.clear()
        lst_date2.clear()
        lst_date_goal_val.clear()
        lst_date_val.clear()
        lst_date_goal_val_2.clear()


        do {
            lst_date.add("" + lst_month[start_calendar[Calendar.MONTH]] + "-" + start_calendar[Calendar.YEAR])
            lst_date2.add(stringHelper!!.get_2_point("" + (start_calendar[Calendar.MONTH] + 1)) 
                    + "-" + start_calendar[Calendar.YEAR])

            start_calendar.add(Calendar.MONTH, 1)
        } while (start_calendar.timeInMillis <= end_calendar.timeInMillis)


        var avg_day_counter = 0
        var avg_total_drink = 0.0
        var avg_frequency_counter = 0
        var avg_total_goal = 0.0


        for (i in lst_date.indices) {
            val arr_data: ArrayList<HashMap<String, String>> =
                sqliteHelper!!.getdata("stats", "date like '%" + lst_date2[i] + "%'")

            var total_drink = 0f
            var total_goal = 0f

            val lst_unique_date: MutableList<String?> = ArrayList()

            for (j in arr_data.indices) {
                if (lst_unique_date.size == 0) lst_unique_date.add(arr_data[j]["date"])
                else if (!lst_unique_date.contains(arr_data[j]["date"])) lst_unique_date.add(
                    arr_data[j]["date"]
                )

                if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
                    total_drink += arr_data[j]["n_intook"]!!.toFloat()

                    if (arr_data[j]["n_intook"]!!.toDouble() > 0) avg_frequency_counter++
                } else {
                    total_drink += arr_data[j]["n_intook_OZ"]!!.toFloat()

                    if (arr_data[j]["n_intook"]!!.toDouble() > 0) avg_frequency_counter++
                }
            }

            avg_day_counter += lst_unique_date.size

            for (t in lst_unique_date.indices) {
                val arr_data22: ArrayList<HashMap<String, String>> =
                    sqliteHelper!!.getdata(
                        "stats",
                        "date='" + lst_unique_date[t] + "'",
                        "id",
                        1
                    )

                if (arr_data22.size > 0) {
                    if (AppUtils.WATER_UNIT_VALUE.equals("ml",true))
                            total_goal += arr_data[0]["n_totalintake"]!!.toFloat()
                    else total_goal += arr_data[0]["n_totalintake_OZ"]!!.toFloat()
                }
            }

            avg_total_goal += total_goal.toDouble()

            avg_total_drink += total_drink.toDouble()

            var avg_drink = 0

            avg_drink =
                if (lst_unique_date.size > 0) Math.round(total_drink / lst_unique_date.size)
                else Math.round(total_drink)

            lst_date_val.add(avg_drink)

            var avg_goal = 0

            avg_goal =
                if (lst_unique_date.size > 0) Math.round(total_goal / lst_unique_date.size)
                else Math.round(total_goal)

            if (avg_drink == 0 && avg_goal == 0) {
                val ii = SharedPreferencesManager.totalIntake.toInt()

                lst_date_goal_val.add(ii)
                lst_date_goal_val_2.add(ii)
            } else if (avg_drink > avg_goal) {
                lst_date_goal_val.add(0)
                lst_date_goal_val_2.add(avg_goal)
            } else {
                lst_date_goal_val.add(avg_goal - avg_drink)
                lst_date_goal_val_2.add(avg_goal)
            }
        }

        try {
            val avg = Math.round(avg_total_drink / avg_day_counter).toInt()
            val f = ("" + avg_frequency_counter).toFloat() / ("" + avg_day_counter).toFloat()
            val avg_fre = Math.round(f)

            val str: String =
                if (avg_fre > 1) stringHelper!!.get_string(R.string.times) else
                    stringHelper!!.get_string(R.string.time)

            binding.txtAvgIntake.text =
                ("" + avg + " " + AppUtils.WATER_UNIT_VALUE) + "/" + stringHelper!!.get_string(
                R.string.day
            )
            binding.txtDrinkFre.text = "" + avg_fre + " " + str + "/" +
                    stringHelper!!.get_string(R.string.day)
        } catch (e: Exception) {
            binding.txtAvgIntake.text = ("0 " + AppUtils.WATER_UNIT_VALUE) + "/" + 
                    stringHelper!!.get_string(
                R.string.day
            )
            binding.txtDrinkFre.text = ("0 " + stringHelper!!.get_string(R.string.time)) + 
                    "/" + stringHelper!!.get_string(
                R.string.day
            )
        }

        try {
            val avg_com = Math.round((avg_total_drink * 100) / avg_total_goal).toInt()

            binding.txtDrinkCom.text = "$avg_com%"
        } catch (e: Exception) {
            binding.txtDrinkCom.text = "0%"
        }

        d("lst_date_val Y : ", "" + Gson().toJson(lst_date_val))
        d("lst_date_val Y 2 : ", "" + Gson().toJson(lst_date_goal_val))
        d("lst_date_val Y 3 : ", "" + Gson().toJson(lst_date_goal_val_2))
    }

    
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun generateBarDataNew() {
        binding.chart1.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {

                try {
                    if (lst_date_val[e.x.toInt()] > 0) showDayDetailsDialog(e.x.toInt())
                } catch (ex: Exception) {
                    ex.message?.let { e(Throwable(ex), it) }
                }
            }

            override fun onNothingSelected() {
            }
        })

        binding.chart1.clear()

        binding.chart1.description.isEnabled = false

        // if more than 60 entries are displayed in the binding.chart1, no values will be
        // drawn
        binding.chart1.setMaxVisibleValueCount(40)

        binding.chart1.setDrawGridBackground(false)
        binding.chart1.setDrawBarShadow(false)

        binding.chart1.setDrawValueAboveBar(false)
        binding.chart1.isHighlightFullBarEnabled = false

        // change the position of the y-labels
        val leftAxis: YAxis = binding.chart1.axisLeft
        leftAxis.textColor = requireContext().resources.getColor(R.color.rdo_gender_select)

        leftAxis.setAxisMaximum(maxBarGraphVal)


        leftAxis.setAxisMinimum(0f) // this replaces setStartAtZero(true)
        binding.chart1.axisRight.isEnabled = false

        binding.chart1.extraBottomOffset = 20f

        val xLabels: XAxis = binding.chart1.xAxis

        xLabels.setDrawGridLines(false)
        xLabels.isGranularityEnabled = false
        xLabels.position = XAxis.XAxisPosition.BOTTOM
        xLabels.textColor = requireContext().resources.getColor(R.color.rdo_gender_select)
        xLabels.labelRotationAngle = -90f
        xLabels.setLabelCount(12)

        val xAxisFormatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                try {
                    if (lst_month_display.size > value.toInt()) return lst_month_display[value.toInt()]
                } catch (e: Exception) {
                    e.message?.let { e(Throwable(e), it) }
                }
                return "N/A"
            }
        }

        xLabels.valueFormatter = xAxisFormatter
        
        val l: Legend = binding.chart1.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.formSize = 8f
        l.formToTextSpace = 4f
        l.xEntrySpace = 6f
        l.isEnabled = false


        val values: ArrayList<BarEntry> = ArrayList<BarEntry>()
        
        for (i in lst_date.indices) {
            val val1 = lst_date_val[i].toFloat()
            val val2 = lst_date_goal_val[i].toFloat()

            d("========", "$val1 @@@ $val2")
            
            values.add(
                BarEntry(
                    i.toFloat(),
                    val1,
                    resources.getDrawable(R.drawable.ic_launcher_background)
                )
            )
        }

        val set1: BarDataSet

        if (binding.chart1.data != null &&
            binding.chart1.data.getDataSetCount() > 0
        ) {
            set1 = binding.chart1.data.getDataSetByIndex(0) as BarDataSet
            set1.setValues(values)
            set1.highLightAlpha = 50
            set1.setDrawValues(false)
            binding.chart1.data.notifyDataChanged()
            binding.chart1.notifyDataSetChanged()
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
                        if (value == 0f) return ""
                        else {
                            for (k in lst_date_goal_val.indices) {
                                if (lst_date_goal_val[k] == value.toInt()) {
                                    return "" + lst_date_goal_val_2[k]
                                }
                            }
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
            binding.chart1.setData(data)
        }

        binding.chart1.animateY(1500)

        binding.chart1.setPinchZoom(false)
        binding.chart1.setScaleEnabled(false)
        binding.chart1.invalidate()
    }

    private val maxBarGraphVal: Float
        get() {
            var drink_val = 0f

            for (k in lst_date_val.indices) {
                if (k == 0) {
                    drink_val = ("" + lst_date_val[k]).toFloat()
                    continue
                }

                if (drink_val < ("" + lst_date_val[k]).toFloat()) drink_val =
                    ("" + lst_date_val[k]).toFloat()
            }

            var goal_val = 0f

            for (k in lst_date_goal_val_2.indices) {
                if (k == 0) {
                    goal_val = ("" + lst_date_goal_val_2[k]).toFloat()
                    continue
                }

                if (goal_val < ("" + lst_date_goal_val_2[k]).toFloat()) goal_val =
                    ("" + lst_date_goal_val_2[k]).toFloat()
            }

            val singleUnit =
                if (AppUtils.WATER_UNIT_VALUE.equals("ml")) 1000 else 35

            var max_val = if (drink_val < goal_val) goal_val else drink_val

            if (drink_val < 1) max_val = (singleUnit * 3).toFloat()

            max_val = ((((max_val / singleUnit).toInt()) + 1) * singleUnit).toFloat()

            return max_val
        }


    private val colors: IntArray
        get() {
            // have as many colors as stack-values per entry

            val colors = IntArray(1)

            colors[0] = requireContext().resources.getColor(R.color.rdo_gender_select)
            
            return colors
        }


    @SuppressLint("SetTextI18n")
    fun showDayDetailsDialog(position: Int) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)

        val view: View =
            LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_day_info_chart, null, false)

        val txt_date: AppCompatTextView = view.findViewById<AppCompatTextView>(R.id.txt_date)
        val txt_goal: AppCompatTextView = view.findViewById<AppCompatTextView>(R.id.txt_goal)
        val txt_consumed: AppCompatTextView =
            view.findViewById<AppCompatTextView>(R.id.txt_consumed)
        val txt_frequency: AppCompatTextView =
            view.findViewById<AppCompatTextView>(R.id.txt_frequency)
        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)
        
        txt_date.text = AppUtils.FormateDateFromString("MMM-yyyy", 
            "MMM yyyy", lst_date[position])
        txt_goal.text = ("" + lst_date_goal_val_2[position] + " " +
                AppUtils.WATER_UNIT_VALUE) + "/" + stringHelper!!.get_string(
            R.string.day
        )
        txt_consumed.text = ("" + lst_date_val[position] + " " +
                AppUtils.WATER_UNIT_VALUE) + "/" + stringHelper!!.get_string(
            R.string.day
        )
        val arr_data: ArrayList<HashMap<String, String>> =
            sqliteHelper!!.getdata("stats", "date like '%" + lst_date2[position] + "%'")

        val lst_unique_date: MutableList<String?> = ArrayList()


        for (j in arr_data.indices) {
            if (lst_unique_date.size == 0) lst_unique_date.add(arr_data[j]["date"])
            else if (!lst_unique_date.contains(arr_data[j]["date"])) lst_unique_date.add(
                arr_data[j]["date"]
            )
        }

        var total_drink_cnt = 0
        for (t in lst_unique_date.indices) {
            val arr_data2: ArrayList<HashMap<String, String>> = sqliteHelper!!.getdata(
                "stats",
                "date ='" + lst_unique_date[t] + "'"
            )

            total_drink_cnt += arr_data2.size
        }

        if (lst_unique_date.size > 0) {
            val f = ("" + total_drink_cnt).toFloat() / ("" + lst_unique_date.size).toFloat()
            val avg_fre = Math.round(f)

            val str: String =
                if (avg_fre > 1) stringHelper!!.get_string(R.string.times) else
                    stringHelper!!.get_string(R.string.time)
            txt_frequency.text = avg_fre.toString() + " " + str + "/" + 
                    stringHelper!!.get_string(R.string.day)
        } else {
            txt_frequency.text = (0.toString() + " " + 
                    stringHelper!!.get_string(R.string.time)) + "/" + stringHelper!!.get_string(
                R.string.day
            )
        }

        img_cancel.setOnClickListener { dialog.dismiss() }

        dialog.setOnDismissListener { binding.chart1.highlightValue(position.toFloat(), -1) }

        dialog.setContentView(view)

        dialog.show()
    }


    private fun generateDataNew() {
        run {
            // background color
            binding.chartNew.setBackgroundColor(Color.WHITE)

            binding.chartNew.clear()

            // disable description text
            binding.chartNew.description.isEnabled = false

            // enable touch gestures
            binding.chartNew.setTouchEnabled(true)

            // set listeners
            binding.chartNew.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry, h: Highlight) {
                    i("Entry selected", e.toString())
                    i(
                        "LOW HIGH",
                        "low: " + binding.chartNew.getLowestVisibleX() + ", high: " +
                                binding.chartNew.getHighestVisibleX()
                    )
                    i(
                        "MIN MAX",
                        "xMin: " + binding.chartNew.xChartMin + ", xMax: " +
                                binding.chartNew.xChartMax + ", yMin: " +
                                binding.chartNew.yChartMin + ", yMax: " +
                                binding.chartNew.yChartMax
                    )

                    i("Entry selected", lst_date2[e.x.toInt()])
                }

                override fun onNothingSelected() {
                }
            })
            binding.chartNew.setDrawGridBackground(false)

            // enable scaling and dragging
            binding.chartNew.setDragEnabled(true)
            binding.chartNew.setScaleEnabled(true)
            // force pinch zoom along both axis
            binding.chartNew.setPinchZoom(true)
        }

        var xAxis: XAxis
        run {
            // // X-Axis Style // //
            xAxis = binding.chartNew.xAxis

            xAxis.position = XAxis.XAxisPosition.BOTTOM

            xAxis.labelRotationAngle = -90f

            xAxis.setLabelCount(lst_date.size, true)

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    try {
                        if (lst_date.size > value.toInt()) return lst_date[value.toInt()]
                    } catch (e: Exception) {
                        e.message?.let { e(Throwable(e), it) }
                    }
                    return "N/A"
                }
            }
            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 0f, 0f)
        }

        var yAxis: YAxis
        run {
            // // Y-Axis Style // //
            yAxis = binding.chartNew.axisLeft

            // disable dual axis (only use LEFT axis)
            binding.chartNew.axisRight.isEnabled = false

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 0f, 0f)

            // axis range
            yAxis.setAxisMaximum(maxGraphVal)
            yAxis.setAxisMinimum(-50f)
        }


        // add data
        setData(lst_date.size)

        // draw points over time
        binding.chartNew.animateY(1500)

        // get the legend (only possible after setting data)
        val l: Legend = binding.chartNew.legend

        // draw legend entries as lines
        l.form = Legend.LegendForm.LINE

        binding.chartNew.isHorizontalScrollBarEnabled = true
    }

    private val maxGraphVal: Float
        get() {
            //return 120;
            var `val` = 1f

            for (k in lst_date_val.indices) {
                if (k == 0) {
                    `val` = ("" + lst_date_val[k]).toFloat()
                    continue
                }

                if (`val` < ("" + lst_date_val[k]).toFloat()) `val` =
                    ("" + lst_date_val[k]).toFloat()
            }

            return `val` + 100
        }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setData(count: Int) {
        val values = ArrayList<Entry>()

        for (i in 0 until count) {
            val r = Random()
            var i1 = r.nextInt(100 - 10) + 10

            i1 = lst_date_val[i]

            val `val` = ("" + i1).toFloat()
            values.add(Entry(i.toFloat(), `val`, resources.getDrawable(R.drawable.ic_add)))
        }

        val set1: LineDataSet

        if (binding.chartNew.data != null &&
            binding.chartNew.data.getDataSetCount() > 0
        ) {
            set1 = binding.chartNew.data.getDataSetByIndex(0) as LineDataSet
            set1.setValues(values)
            set1.notifyDataSetChanged()
            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            binding.chartNew.data.notifyDataChanged()
            binding.chartNew.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "")

            set1.mode = LineDataSet.Mode.CUBIC_BEZIER

            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 0f, 0f)

            // black lines and points
            set1.setColor(getThemeColor(requireActivity()))
            set1.setCircleColor(getThemeColor(requireActivity()))

            // line thickness and point size
            set1.setLineWidth(2f)
            set1.circleRadius = 5f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 0f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 0f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.setFillFormatter { dataSet, dataProvider -> binding.chartNew.axisLeft.axisMinimum }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable: Drawable? =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.line_chart_fade_back)

                val gd: GradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    getThemeColorArray(requireActivity())
                )

                set1.fillDrawable = gd
            } else {
                set1.setFillColor(Color.BLACK)
            }

            val dataSets: ArrayList<ILineDataSet> = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data: LineData = LineData(dataSets)

            // set data
            binding.chartNew.setData(data)
        }
    }
}