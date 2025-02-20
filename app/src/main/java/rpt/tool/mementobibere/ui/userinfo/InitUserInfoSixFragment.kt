package rpt.tool.mementobibere.ui.userinfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.wdullaer.materialdatetimepicker.time.Timepoint
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentInitUserInfoSixBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import android.app.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InitUserInfoSixFragment :
    BaseFragment<FragmentInitUserInfoSixBinding>(FragmentInitUserInfoSixBinding::inflate) {

    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
    var from_hour: Int = 8
    var from_minute: Int = 0
    var to_hour: Int = 22
    var to_minute: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        body()
        setCount()
    }


    @Deprecated("Deprecated in Java")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            setCount()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun body() {
        binding.rdo15.text = "15 " + requireContext().getString(R.string.str_min)
        binding.rdo30.text = "30 " + requireContext().getString(R.string.str_min)
        binding.rdo45.text = "45 " + requireContext().getString(R.string.str_min)
        binding.rdo60.text = "1 " + requireContext().getString(R.string.str_hour)

        wakeupTime = SharedPreferencesManager.wakeUpTime
        sleepingTime = SharedPreferencesManager.sleepingTime

        val is24h = android.text.format.DateFormat.is24HourFormat(requireContext())

        binding.txtWakeupTime.setOnClickListener {
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

                    binding.txtWakeupTime.text =
                        String.format(Locale.getDefault(),"%02d:%02d", selectedHour, selectedMinute)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24h
            )
            mTimePicker.setTitle(getString(R.string.select_wakeup_time))
            mTimePicker.show()
        }

        binding.txtBedTime.setOnClickListener {
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

                    binding.txtBedTime.text =
                        String.format(Locale.getDefault(),"%02d:%02d", selectedHour, selectedMinute)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24h
            )
            mTimePicker.setTitle(getString(R.string.select_bed_time))
            mTimePicker.show()
        }

        binding.rdo15.setOnClickListener { setCount() }

        binding.rdo30.setOnClickListener { setCount() }

        binding.rdo45.setOnClickListener { setCount() }

        binding.rdo60.setOnClickListener { setCount() }
    }


    private val isNextDayEnd: Boolean
        get() {
            val simpleDateFormat =
                SimpleDateFormat("hh:mm a", Locale.getDefault())

            var date1: Date? = null
            var date2: Date? = null
            try {
                date1 =
                    simpleDateFormat.parse(binding.txtWakeupTime.getText().toString().trim { it <= ' ' })
                date2 = simpleDateFormat.parse(binding.txtBedTime.getText().toString().trim { it <= ' ' })

                return date1.time > date2.time
            } catch (e: Exception) {
                e.message?.let { e(Throwable(e), it) }
            }

            return false
        }

    private fun setCount() {

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

        val mills = endTime.timeInMillis - startTime.timeInMillis

        val hours = (mills / (1000 * 60 * 60)).toInt()
        val mins = ((mills / (1000 * 60)) % 60).toInt()
        val total_minutes = ((hours * 60) + mins).toFloat()

        val interval =
            if (binding.rdo15.isChecked) 15 
            else if (binding.rdo30.isChecked) 30 
            else if (binding.rdo45.isChecked) 45 else 60

        var consume = 0 // @@@@@
        if (total_minutes > 0) consume =
            Math.round(AppUtils.DAILY_WATER_VALUE / (total_minutes / interval))

        val unit = if (SharedPreferencesManager.personWeightUnit) "ml" else "fl oz"

        binding.lblMessage.text =
            requireContext().getString(R.string.str_goal_consume).replace("$1",
                "$consume $unit")
                .replace("$2", "" + AppUtils.DAILY_WATER_VALUE + " " + unit)

        SharedPreferencesManager.wakeUpTime = wakeupTime
        SharedPreferencesManager.sleepingTime = sleepingTime

        SharedPreferencesManager.notificationFreq = interval.toFloat()

        if (consume > AppUtils.DAILY_WATER_VALUE)
            SharedPreferencesManager.ignoreNextStep = true
        else if (consume == 0) SharedPreferencesManager.ignoreNextStep = true
        else SharedPreferencesManager.ignoreNextStep = false

    }
}