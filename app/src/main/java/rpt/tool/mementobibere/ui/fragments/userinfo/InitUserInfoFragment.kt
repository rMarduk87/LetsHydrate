package rpt.tool.mementobibere.ui.fragments.userinfo

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.InitUserInfoFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar

class InitUserInfoFragment:
    BaseFragment<InitUserInfoFragmentBinding>(InitUserInfoFragmentBinding::inflate) {

    private var weight: String = ""
    private var workTime: String = ""
    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
    private lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val is24h = android.text.format.DateFormat.is24HourFormat(requireContext())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

        wakeupTime = sharedPref.getLong(AppUtils.WAKEUP_TIME, 1558323000000)
        sleepingTime = sharedPref.getLong(AppUtils.SLEEPING_TIME_KEY, 1558369800000)

        binding.etWakeUpTime.editText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = wakeupTime

            val mTimePicker: TimePickerDialog = TimePickerDialog(
                requireContext(),
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->

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

            val mTimePicker: TimePickerDialog = TimePickerDialog(
                requireContext(),
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->

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

        binding.btnContinue.setOnClickListener {

            val imm: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.initUserInfoParentLayout.windowToken, 0)

            weight = binding.etWeight.editText!!.text.toString()
            workTime = binding.etWorkTime.editText!!.text.toString()

            when {
                TextUtils.isEmpty(weight) -> showError(getString(R.string.please_input_your_weight),it)

                weight.toInt() > 200 || weight.toInt() < 20 ->
                    showError(getString(R.string.please_input_a_valid_weight), it)

                TextUtils.isEmpty(workTime) -> showError(
                    getString(R.string.please_input_your_workout_time),
                    it
                )

                workTime.toInt() > 500 || workTime.toInt() < 0 ->
                    showError(getString(R.string.please_input_a_valid_workout_time), it)

                else -> {

                    val editor = sharedPref.edit()
                    editor.putInt(AppUtils.WEIGHT_KEY, weight.toInt())
                    editor.putInt(AppUtils.WORK_TIME_KEY, workTime.toInt())
                    editor.putLong(AppUtils.WAKEUP_TIME, wakeupTime)
                    editor.putLong(AppUtils.SLEEPING_TIME_KEY, sleepingTime)
                    editor.putBoolean(AppUtils.FIRST_RUN_KEY, false)

                    val totalIntake = AppUtils.calculateIntake(weight.toInt(), workTime.toInt())
                    val df = DecimalFormat("#")
                    df.roundingMode = RoundingMode.CEILING
                    editor.putInt(AppUtils.TOTAL_INTAKE, df.format(totalIntake).toInt())

                    editor.apply()
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun showError(error: String, view: View) {
        val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        val customSnackView: View =
            layoutInflater.inflate(R.layout.error_toast_layout, null)
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackbarLayout = snackBar.view as Snackbar.SnackbarLayout

        val text = customSnackView.findViewById<TextView>(R.id.tvMessage)
        text.text = error

        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(customSnackView, 0)
        snackBar.show()
    }
}