package rpt.tool.mementobibere.ui.reminder

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentReminderBinding
import rpt.tool.mementobibere.utils.data.appmodel.IntervalModel
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.view.adapters.IntervalAdapter
import java.util.Calendar
import java.util.Locale


class ReminderFragment : BaseFragment<FragmentReminderBinding>(FragmentReminderBinding::inflate) {

    private var is24h: Boolean = false
    var from_hour: Int = 0
    var from_minute: Int = 0
    var to_hour:Int = 0
    var to_minute:Int = 0
    var interval: Int = 30
    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
    var lst_interval: MutableList<String> = ArrayList()
    var lst_intervals: MutableList<IntervalModel> = ArrayList()
    var intervalAdapter: IntervalAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.navigationBarColor =
            requireContext().resources.getColor(R.color.str_green_card)
        is24h = android.text.format.DateFormat.is24HourFormat(requireContext())
        sleepingTime = SharedPreferencesManager.sleepingTime
        wakeupTime = SharedPreferencesManager.wakeUpTime
        interval = SharedPreferencesManager.notificationFreq.toInt()
        body()

        var str: String = requireContext().getString(R.string.str_bed_time)
        str = str.substring(0, 1).uppercase(Locale.getDefault()) + "" + str.substring(1).lowercase(
            Locale.getDefault()
        )
        val cal = Calendar.getInstance()
        cal.timeInMillis = sleepingTime
        val bedSstr = " " + String.format(
            Locale.getDefault(),
            "%02d:%02d",
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE)
        )

        binding.lblbt.text = str
        binding.lblBedTime.text = bedSstr

        //=======
        str = requireContext().getString(R.string.str_wakeup_time)
        str = str.substring(0, 1).uppercase(Locale.getDefault()) + "" + str.substring(1).lowercase(
            Locale.getDefault()
        )

        cal.timeInMillis = wakeupTime
        val wakeStr = " " +
            String.format(Locale.getDefault(),
                "%02d:%02d",
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)

        )

        binding.lblwt.text = str
        binding.lblWakeupTime.text = wakeStr

    }

    @SuppressLint("SetTextI18n")
    fun body(){
        binding.lblWakeupTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = wakeupTime

            val mTimePicker = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)
                    wakeupTime = time.timeInMillis
                    from_hour = selectedHour
                    from_minute = selectedMinute

                    binding.lblWakeupTime.text =
                        String.format(Locale.getDefault(),"%02d:%02d", selectedHour, selectedMinute)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24h
            )
            mTimePicker.setTitle(getString(R.string.select_wakeup_time))
            mTimePicker.show()
        }

        binding.lblBedTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = sleepingTime

            val mTimePicker = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)
                    sleepingTime = time.timeInMillis
                    to_hour = selectedHour
                    to_minute = selectedMinute

                    binding.lblBedTime.text = String.format(Locale.getDefault(),"%02d:%02d", selectedHour, selectedMinute)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24h
            )
            mTimePicker.setTitle(getString(R.string.select_bed_time))
            mTimePicker.show()
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
            SharedPreferencesManager.wakeUpTime = wakeupTime
            SharedPreferencesManager.sleepingTime = sleepingTime
        }
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