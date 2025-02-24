package rpt.tool.letshydrate.ui.reminder

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wdullaer.materialdatetimepicker.time.Timepoint
import rpt.tool.letshydrate.BaseFragment
import rpt.tool.letshydrate.MainActivity
import rpt.tool.letshydrate.R
import rpt.tool.letshydrate.databinding.FragmentReminderBinding
import rpt.tool.letshydrate.utils.AppUtils
import rpt.tool.letshydrate.utils.data.appmodel.IntervalModel
import rpt.tool.letshydrate.utils.log.e
import rpt.tool.letshydrate.utils.managers.SharedPreferencesManager
import rpt.tool.letshydrate.utils.view.adapters.IntervalAdapter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ReminderFragment : BaseFragment<FragmentReminderBinding>(FragmentReminderBinding::inflate) {

    var from_hour: Int = 0
    var from_minute: Int = 0
    var to_hour:Int = 0
    var to_minute:Int = 0
    var interval: Int = 30
    var lst_interval: MutableList<String> = ArrayList()
    var lst_intervals: MutableList<IntervalModel> = ArrayList()
    var intervalAdapter: IntervalAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.navigationBarColor =
            requireContext().resources.getColor(R.color.str_green_card)
        interval = SharedPreferencesManager.notificationFreq.toInt()
        body()

        var str: String = requireContext().getString(R.string.str_bed_time)
        str = str.substring(0, 1).uppercase(Locale.getDefault()) + "" + str.substring(1).lowercase(
            Locale.getDefault()
        )

        binding.lblbt.text = str
        binding.lblBedTime.text = SharedPreferencesManager.bedTime

        //=======
        str = requireContext().getString(R.string.str_wakeup_time)
        str = str.substring(0, 1).uppercase(Locale.getDefault()) + "" + str.substring(1).lowercase(
            Locale.getDefault()
        )


        binding.lblwt.text = str
        binding.lblWakeupTime.text = SharedPreferencesManager.wakeUpTimeNew

    }

    @SuppressLint("SetTextI18n")
    fun body(){
        binding.lblWakeupTime.setOnClickListener {
            openAutoTimePicker(binding.lblWakeupTime, true)
        }

        binding.lblBedTime.setOnClickListener {
            openAutoTimePicker(binding.lblBedTime, false)
        }

        binding.lblInterval.setOnClickListener { openIntervalPicker() }

        lst_interval.clear()

        lst_interval.add("15 " + requireContext().getString(R.string.str_minutes))
        lst_interval.add("30 " + requireContext().getString(R.string.str_minutes))
        lst_interval.add("45 " + requireContext().getString(R.string.str_minutes))
        lst_interval.add("60 " + requireContext().getString(R.string.str_minutes))
        lst_interval.add("90 " + requireContext().getString(R.string.str_minutes))
        lst_interval.add("120 " + requireContext().getString(R.string.str_minutes))

        binding.include1.leftIconBlock.setOnClickListener { finish() }
        binding.include1.rightIconBlock.visibility = View.GONE

        interval = SharedPreferencesManager.notificationFreq.toInt()

        if(interval == 60)
            binding.lblInterval.text = "1 "+requireContext().getString(R.string.str_hour)
        else
            binding.lblInterval.text = interval.toString() + " "+
                    requireContext().getString(R.string.str_min)

        binding.saveReminder.setOnClickListener{
            SharedPreferencesManager.notificationFreq = interval.toFloat()
            setHour()
        }
    }

    private fun setHour() {
        val startTime = Calendar.getInstance(Locale.getDefault())
        startTime[Calendar.HOUR_OF_DAY] = from_hour
        startTime[Calendar.MINUTE] = from_minute
        startTime[Calendar.SECOND] = 0

        val endTime = Calendar.getInstance(Locale.getDefault())
        endTime[Calendar.HOUR_OF_DAY] = to_hour
        endTime[Calendar.MINUTE] = to_minute
        endTime[Calendar.SECOND] = 0

        // @@@@@
        if (isNextDayEnd) endTime.add(Calendar.DATE, 1)

        SharedPreferencesManager.wakeUpTimeNew = binding.lblWakeupTime.getText().toString().trim()
        SharedPreferencesManager.wakeUpTimeHour = from_hour
        SharedPreferencesManager.wakeUpTimeMinute = from_minute

        SharedPreferencesManager.bedTime = binding.lblBedTime.getText().toString().trim()
        SharedPreferencesManager.bedTimeHour = to_hour
        SharedPreferencesManager.bedTimeMinute = to_minute

    }

    private val isNextDayEnd: Boolean
        get() {
            val simpleDateFormat =
                SimpleDateFormat("hh:mm a", Locale.getDefault())

            var date1: Date? = null
            var date2: Date? = null
            try {
                date1 =
                    simpleDateFormat.parse(binding.lblWakeupTime.getText().toString().trim { it <= ' ' })
                date2 = simpleDateFormat.parse(binding.lblBedTime.getText().toString().trim { it <= ' ' })

                return date1.time > date2.time
            } catch (e: Exception) {
                e.message?.let { e(Throwable(e), it) }
            }

            return false
        }

    @SuppressLint("SetTextI18n")
    fun openAutoTimePicker(appCompatTextView: AppCompatTextView, isFrom: Boolean) {
        val onTimeSetListener: com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener =
            com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, second ->
                var formatedDate = ""
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
                val sdfs = SimpleDateFormat("hh:mm a", Locale.US)
                val dt: Date
                var time = ""

                try {
                    if (isFrom) {
                        from_hour = hourOfDay
                        from_minute = minute
                    } else {
                        to_hour = hourOfDay
                        to_minute = minute
                    }

                    time = "$hourOfDay:$minute:00"
                    dt = sdf.parse(time)!!
                    formatedDate = sdfs.format(dt)
                    appCompatTextView.text = "" + formatedDate

                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

        val now = Calendar.getInstance(Locale.US)

        if (isFrom) {
            now[Calendar.HOUR_OF_DAY] = from_hour
            now[Calendar.MINUTE] = from_minute
        } else {
            now[Calendar.HOUR_OF_DAY] = to_hour
            now[Calendar.MINUTE] = to_minute
        }
        val tpd: com.wdullaer.materialdatetimepicker.time.TimePickerDialog
        if (!DateFormat.is24HourFormat(requireActivity())) {
            //12 hrs format
            tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                onTimeSetListener,
                now[Calendar.HOUR_OF_DAY],
                now[Calendar.MINUTE], false
            )

            tpd.setSelectableTimes(generateTimepoints(23.50, 30))

            tpd.setMaxTime(23, 30, 0)
        } else {
            //24 hrs format
            tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                onTimeSetListener,
                now[Calendar.HOUR_OF_DAY],
                now[Calendar.MINUTE], true
            )

            tpd.setSelectableTimes(generateTimepoints(23.50, 30))

            tpd.setMaxTime(23, 30, 0)
        }


        tpd.accentColor = getThemeColor(requireContext())
        tpd.show(requireActivity().fragmentManager, "Datepickerdialog")
        tpd.accentColor = getThemeColor(requireContext())
    }

    private fun generateTimepoints(maxHour: Double, minutesInterval: Int): Array<Timepoint> {
        val lastValue = (maxHour * 60).toInt()

        val timepoints: MutableList<Timepoint> = ArrayList()

        var minute = 0
        while (minute <= lastValue) {
            val currentHour = minute / 60
            val currentMinute = minute - (if (currentHour > 0) (currentHour * 60) else 0)
            if (currentHour == 24) {
                minute += minutesInterval
                continue
            }
            timepoints.add(Timepoint(currentHour, currentMinute))
            minute += minutesInterval
        }
        return timepoints.toTypedArray<Timepoint>()
    }

    private fun finish() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
    }

    private fun loadInterval() {
        lst_intervals.clear()

        lst_intervals.add(getIntervalModel(15, "15 " +
                requireContext().getString(R.string.str_min)))
        lst_intervals.add(getIntervalModel(30, "30 " +
                requireContext().getString(R.string.str_min)))
        lst_intervals.add(getIntervalModel(45, "45 " +
                requireContext().getString(R.string.str_min)))
        lst_intervals.add(getIntervalModel(60, "1 " +
                requireContext().getString(R.string.str_hour)))
    }

    private fun getIntervalModel(index: Int, name: String?): IntervalModel {
        val intervalModel = IntervalModel()
        intervalModel.id = index
        intervalModel.name = name
        intervalModel.isSelected(interval == index)

        return intervalModel
    }


    private fun openIntervalPicker() {
        loadInterval()

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val view: View = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_pick_interval, null, false)


        val btn_cancel = view.findViewById<RelativeLayout>(R.id.btn_cancel)
        val btn_save = view.findViewById<RelativeLayout>(R.id.btn_save)


        val intervalRecyclerView = view.findViewById<RecyclerView>(R.id.intervalRecyclerView)

        intervalAdapter = IntervalAdapter(requireActivity(), lst_intervals, object : IntervalAdapter.CallBack {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClickSelect(time: IntervalModel?, position: Int) {
                for (k in 0 until lst_intervals.size) {
                    lst_intervals[k].isSelected(false)
                }

                lst_intervals[position].isSelected(true)
                intervalAdapter!!.notifyDataSetChanged()
            }
        })

        intervalRecyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        intervalRecyclerView.adapter = intervalAdapter


        btn_cancel.setOnClickListener { dialog.dismiss() }

        btn_save.setOnClickListener {
            for (k in 0 until lst_intervals.size) {
                if (lst_intervals[k].isSelected) {
                    interval = lst_intervals[k].id
                    binding.lblInterval.text = lst_intervals[k].name
                    break
                }
            }
            dialog.dismiss()
        }

        dialog.setContentView(view)

        dialog.show()
    }
}