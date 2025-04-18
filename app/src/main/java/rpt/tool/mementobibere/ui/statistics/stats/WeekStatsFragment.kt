package rpt.tool.mementobibere.ui.statistics.stats

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import com.google.gson.Gson
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentStatsWeekBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import java.util.Calendar
import java.util.Locale
import java.util.Random
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

class WeekStatsFragment : BaseFragment<FragmentStatsWeekBinding>(FragmentStatsWeekBinding::inflate) {

    private var isUS: Boolean = false
    var lst_date: MutableList<String> = ArrayList()
    var lst_date_val: MutableList<Int> = ArrayList()
    var lst_date_goal_val: MutableList<Int> = ArrayList()
    var lst_date_goal_val_2: MutableList<Int> = ArrayList()
    var lst_week: MutableList<String> = ArrayList()
    var current_start_calendar: Calendar? = null
    var current_end_calendar: Calendar? = null
    var start_calendarN: Calendar? = null
    var end_calendarN: Calendar? = null
    var sqliteHelper: SqliteHelper? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isUS = Locale.getDefault().country.equals("US",true)

        lst_week.clear()
        lst_week.add(requireContext().getString(R.string.sun))
        lst_week.add(requireContext().getString(R.string.mon))
        lst_week.add(requireContext().getString(R.string.tue))
        lst_week.add(requireContext().getString(R.string.wed))
        lst_week.add(requireContext().getString(R.string.thu))
        lst_week.add(requireContext().getString(R.string.fri))
        lst_week.add(requireContext().getString(R.string.sat))

        setCurrentWeekInfo()

        body()
    }

    private fun setCurrentWeekInfo() {

        current_start_calendar = if(isUS)Calendar.getInstance(Locale.US) else
            Calendar.getInstance(Locale.getDefault())
        current_start_calendar!!.set(Calendar.DAY_OF_WEEK, if(isUS)1 else 2)
        current_start_calendar!!.set(Calendar.HOUR_OF_DAY, 0)
        current_start_calendar!!.set(Calendar.MINUTE, 0)
        current_start_calendar!!.set(Calendar.SECOND, 0)
        current_start_calendar!!.set(Calendar.MILLISECOND, 0)

        current_end_calendar = if(isUS)Calendar.getInstance(Locale.US) else
            Calendar.getInstance(Locale.getDefault())
        current_end_calendar!!.set(Calendar.DAY_OF_WEEK, if(isUS)7 else 1)
        current_end_calendar!!.set(Calendar.HOUR_OF_DAY, 23)
        current_end_calendar!!.set(Calendar.MINUTE, 59)
        current_end_calendar!!.set(Calendar.SECOND, 59)
        current_end_calendar!!.set(Calendar.MILLISECOND, 999)

        d(
            "START===========END",
            """
current_start_calendar : ${current_start_calendar!!.timeInMillis}  
 current_start_calendar : ${current_end_calendar!!.timeInMillis}"""
        )
    }

    private fun body() {
        
        sqliteHelper = SqliteHelper(requireContext())
        
        start_calendarN = if(isUS)Calendar.getInstance(Locale.US) else
            Calendar.getInstance(Locale.getDefault())
        start_calendarN!!.set(Calendar.DAY_OF_WEEK, if(isUS)1 else 2)
        start_calendarN!!.set(Calendar.HOUR_OF_DAY, 0)
        start_calendarN!!.set(Calendar.MINUTE, 0)
        start_calendarN!!.set(Calendar.SECOND, 0)
        start_calendarN!!.set(Calendar.MILLISECOND, 0)

        end_calendarN = if(isUS)Calendar.getInstance(Locale.US) else
            Calendar.getInstance(Locale.getDefault())
        end_calendarN!!.set(Calendar.DAY_OF_WEEK, if(isUS)7 else 1)
        end_calendarN!!.set(Calendar.HOUR_OF_DAY, 23)
        end_calendarN!!.set(Calendar.MINUTE, 59)
        end_calendarN!!.set(Calendar.SECOND, 59)
        end_calendarN!!.set(Calendar.MILLISECOND, 999)


        loadData(start_calendarN!!, end_calendarN!!)
        
        binding.imgPre.setOnClickListener {
            start_calendarN!!.add(Calendar.DATE, -7)
            end_calendarN!!.add(Calendar.DATE, -7)

            loadData(start_calendarN!!, end_calendarN!!)
            generateBarDataNew()
        }
        
        binding.imgNext.setOnClickListener(View.OnClickListener {
            start_calendarN!!.add(Calendar.DATE, 7)
            end_calendarN!!.add(Calendar.DATE, 7)

            d(
                "MIN_MAX_DATE 2.1 : ", "" +
                        start_calendarN!!.timeInMillis +
                        " @@@ " + "" + end_calendarN!!.timeInMillis
            )


            if (start_calendarN!!.timeInMillis >= current_end_calendar!!.timeInMillis) {
                start_calendarN!!.add(Calendar.DATE, -7)
                end_calendarN!!.add(Calendar.DATE, -7)
                return@OnClickListener
            }

            loadData(start_calendarN!!, end_calendarN!!)
            generateBarDataNew()
        })

        generateBarDataNew()
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(start_calendar2: Calendar, end_calendar2: Calendar) {

        val start_calendar = if(isUS)Calendar.getInstance(Locale.US) else
            Calendar.getInstance(Locale.getDefault())
        start_calendar.timeInMillis = start_calendar2.timeInMillis
        val end_calendar = if(isUS)Calendar.getInstance(Locale.US) else
            Calendar.getInstance(Locale.getDefault())
        end_calendar.timeInMillis = end_calendar2.timeInMillis

        binding.lblTitle.text = (AppUtils.getDate(start_calendar.timeInMillis, 
            "dd MMM") + " - "
                + AppUtils.getDate(end_calendar.timeInMillis, "dd MMM"))

        lst_date.clear()
        lst_date_goal_val.clear()
        lst_date_val.clear()
        lst_date_goal_val_2.clear()


        do {
            d(
                "DATE2 : ",
                (("" + AppUtils!!.get_2_point("" + 
                        start_calendar[Calendar.DAY_OF_MONTH])) + "-" + AppUtils!!.get_2_point(
                    "" + (start_calendar[Calendar.MONTH] + 1)
                )) + "-" + start_calendar[Calendar.YEAR]
            )

            lst_date.add(
                (("" + AppUtils!!.get_2_point("" + 
                        start_calendar[Calendar.DAY_OF_MONTH])) + "-" + AppUtils!!.get_2_point(
                    "" + (start_calendar[Calendar.MONTH] + 1)
                )) + "-" + start_calendar[Calendar.YEAR]
            )
            
            start_calendar.add(Calendar.DATE, 1)
        } while (start_calendar.timeInMillis <= end_calendar.timeInMillis)

        var day_counter = 0
        var total_drink = 0.0
        var frequency_counter = 0
        var total_goal = 0.0

        for (i in lst_date.indices) {
            val arr_data: ArrayList<HashMap<String, String>> =
                sqliteHelper!!.getdata("stats", "n_date ='" + lst_date[i] + "'")
            var tot = 0f
            var last_goal: String? = "0"
            for (j in arr_data.indices) {
                if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
                    tot += arr_data[j]["n_intook"]!!.toFloat()
                    last_goal = arr_data[j]["n_totalintake"]

                    if (arr_data[j]["n_intook"]!!.toDouble() > 0) frequency_counter++
                } else {
                    tot += arr_data[j]["n_intook_OZ"]!!.toFloat()
                    last_goal = arr_data[j]["n_totalintake_OZ"]

                    if (arr_data[j]["n_intook_OZ"]!!.toDouble() > 0) frequency_counter++
                }
            }
            lst_date_val.add(tot.toInt())

            if (tot > 0) {
                day_counter++

                total_goal += last_goal!!.toFloat()
            }

            total_drink += tot.toDouble()
            

            if (tot == 0f && Math.round(last_goal!!.toFloat()) == 0) {
                val ii = SharedPreferencesManager.totalIntake.toInt()

                lst_date_goal_val.add(ii)
                lst_date_goal_val_2.add(ii)
            } else if (tot > Math.round(last_goal!!.toFloat())) {
                lst_date_goal_val.add(0)
                lst_date_goal_val_2.add(Math.round(last_goal.toFloat()))
            } else {
                lst_date_goal_val.add(Math.round(last_goal.toFloat()) - tot.toInt())
                lst_date_goal_val_2.add(Math.round(last_goal.toFloat()))
            }
        }

        try {
            val avg = Math.round(total_drink / day_counter).toInt()
            val f = ("" + frequency_counter).toFloat() / ("" + day_counter).toFloat()
            val avg_fre = Math.round(f)


            val str: String =
                if (avg_fre > 1) requireContext().getString(R.string.times) else
                    requireContext().getString(R.string.time)


            binding.txtAvgIntake.text = ("" + avg + " " + AppUtils.WATER_UNIT_VALUE) + "/" + requireContext().getString(
                R.string.day
            )
            binding.txtDrinkFre.text = "" + avg_fre + " " + str + "/" +
                    requireContext().getString(R.string.day)
        } catch (e: Exception) {
            binding.txtAvgIntake.text = ("0 " + AppUtils.WATER_UNIT_VALUE) + "/" + requireContext().getString(
                R.string.day
            )
            binding.txtDrinkFre.text = ("0 " + requireContext().getString(R.string.time)) + "/" +
                    requireContext().getString(
                        R.string.day
                    )
        }

        try {
            val avg_com = Math.round((total_drink * 100) / total_goal).toInt()

            binding.txtDrinkCom.text = "$avg_com%"
        } catch (e: Exception) {
            binding.txtDrinkCom.text = "0%"
        }


        d("lst_date_val : ", "" + Gson().toJson(lst_date_val))
        d("lst_date_val 2 : ", "" + Gson().toJson(lst_date_goal_val))
    }

    private val onValueSelectedRectF: RectF = RectF()

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


        val xAxisFormatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                try {
                    if (lst_week.size > value.toInt()) return lst_week[value.toInt()]
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
                if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) 1000 else 35

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


    @SuppressLint("InflateParams", "SetTextI18n")
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

        
        txt_date.text = AppUtils.FormateDateFromString("dd-MM-yyyy", "dd MMM", lst_date[position])
        txt_goal.text = "" + lst_date_goal_val_2[position] + " " + AppUtils.WATER_UNIT_VALUE
        txt_consumed.text = "" + lst_date_val[position] + " " + AppUtils.WATER_UNIT_VALUE

        val arr_data: ArrayList<HashMap<String, String>> =
            sqliteHelper!!.getdata("stats", "n_date ='" + lst_date[position] + "'")
        val str: String =
            if (arr_data.size > 1) requireContext().getString(R.string.times) else requireContext().getString(R.string.time)
        txt_frequency.text = arr_data.size.toString() + " " + str

        img_cancel.setOnClickListener { dialog.dismiss() }

        dialog.setOnDismissListener { binding.chart1.highlightValue(position.toFloat(), -1) }

        dialog.setContentView(view)

        dialog.show()
    }
}