package rpt.tool.mementobibere.ui.hour

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import rpt.tool.mementobibere.BaseBottomSheetDialog
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.AdjustHourBottomSheetFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import java.util.Calendar

class AdjustHourBottomSheetFragment:
    BaseBottomSheetDialog<AdjustHourBottomSheetFragmentBinding>(AdjustHourBottomSheetFragmentBinding::inflate) {

    private lateinit var sharedPref: SharedPreferences
    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
    private var themeInt : Int = 0

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val is24h = android.text.format.DateFormat.is24HourFormat(requireContext())

        sharedPref = requireContext().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        setBackground()

        wakeupTime = sharedPref.getLong(AppUtils.WAKEUP_TIME_KEY, 1558323000000)
        sleepingTime = sharedPref.getLong(AppUtils.SLEEPING_TIME_KEY, 1558369800000)
        val cal = Calendar.getInstance()
        cal.timeInMillis = wakeupTime
        binding.etWakeUpTime.editText!!.setText(
            String.format(
                "%02d:%02d",
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            )
        )
        cal.timeInMillis = sleepingTime
        binding.etSleepTime.editText!!.setText(
            String.format(
                "%02d:%02d",
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            )
        )

        binding.etWakeUpTime.editText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = wakeupTime

            val mTimePicker = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)
                    wakeupTime = time.timeInMillis

                    binding.etWakeUpTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24h
            )
            mTimePicker.setTitle(getString(R.string.select_wakeup_time))
            mTimePicker.show()
        }


        binding.etSleepTime.editText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = sleepingTime

            val mTimePicker = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)
                    sleepingTime = time.timeInMillis

                    binding.etSleepTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24h
            )
            mTimePicker.setTitle(getString(R.string.select_sleeping_time))
            mTimePicker.show()
        }

        binding.btnUpdate.setOnClickListener {


            when {

                !AppUtils.isValidDate(binding.etSleepTime.editText!!.text.toString(),
                    binding.etWakeUpTime.editText!!.text.toString()) ->
                    showError(getString(R.string.please_input_a_valid_rest_time))

                else -> {

                    val editor = sharedPref.edit()

                    editor.putLong(AppUtils.WAKEUP_TIME_KEY, wakeupTime)
                    editor.putLong(AppUtils.SLEEPING_TIME_KEY, sleepingTime)

                    editor.apply()
                    dismiss()
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showError(error: String) {
        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        val customView: View =
            layoutInflater.inflate(R.layout.error_toast_layout, null)

        val text = customView.findViewById<TextView>(R.id.tvMessage)
        text.text = error
        toast.view = customView
        toast.show()
    }

    private fun setBackground() {
        when(themeInt){
            0-> toLightTheme()
            1-> toDarkTheme()
            2-> toWaterTheme()
        }
    }

    private fun toWaterTheme() {
        setBackgroundColor(requireContext().getColor(R.color.colorSecondaryDarkW))
        binding.textView7.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.btnUpdate.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.etWakeUpTime.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etSleepTime.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etWakeUpTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etSleepTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
    }

    private fun toDarkTheme() {
        setBackgroundColor(requireContext().getColor(R.color.darkGreen))
        binding.textView7.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.btnUpdate.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.etWakeUpTime.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etSleepTime.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etWakeUpTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etSleepTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
    }

    private fun toLightTheme() {
        setBackgroundColor(requireContext().getColor(R.color.colorSecondaryDark))
        binding.textView7.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.btnUpdate.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.etWakeUpTime.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etSleepTime.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etWakeUpTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etSleepTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
    }

    private fun setBackgroundColor(color: Int) {
        binding.bottomSheetParent.setBackgroundColor(color)
    }
}