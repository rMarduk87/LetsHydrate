package rpt.tool.mementobibere.ui.userinfo

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import rpt.tool.mementobibere.BaseBottomSheetDialog
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.EditInfoBottomSheetFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.AppUtils.Companion.NO_UPDATE_UNIT
import rpt.tool.mementobibere.utils.AppUtils.Companion.calculateExtensions
import rpt.tool.mementobibere.utils.extensions.toNumberString
import rpt.tool.mementobibere.utils.helpers.AlarmHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Locale


@Suppress("DEPRECATION")
class EditInfoBottomSheetFragment : BaseBottomSheetDialog<EditInfoBottomSheetFragmentBinding>(EditInfoBottomSheetFragmentBinding::inflate) {

    private lateinit var sharedPref: SharedPreferences
    private var weight: String = ""
    private var workTime: String = ""
    private var customTarget: String = ""
    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
    private var currentUnitint: Int = 0
    private var newUnitint: Int = 0
    private var weightUnit: Int = 0
    private var themeInt : Int = 0
    private var oldWeight: Int = 0
    private var oldWorkTime: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val is24h = android.text.format.DateFormat.is24HourFormat(requireContext())

        sharedPref = requireContext().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

        binding.etWeight.editText!!.setText("" + sharedPref.getInt(AppUtils.WEIGHT_KEY, 0))
        binding.etWorkTime.editText!!.setText("" + sharedPref.getInt(AppUtils.WORK_TIME_KEY, 0))
        binding.etTarget.editText!!.setText("" + sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f).toNumberString())

        oldWeight = sharedPref.getInt(AppUtils.WEIGHT_KEY, 0)
        oldWorkTime = sharedPref.getInt(AppUtils.WORK_TIME_KEY, 0)

        currentUnitint = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        newUnitint = sharedPref.getInt(AppUtils.UNIT_NEW_KEY,0)
        weightUnit = sharedPref.getInt(AppUtils.WEIGHT_UNIT_KEY,0)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        setBackground()

        val unit = calculateExtensions(newUnitint)
        if(Locale.getDefault().language == "de"){
            binding.etTarget.hint = getString(R.string.custom_intake_hint) + " " + unit + " ein"
        }
        else{
            binding.etTarget.hint = getString(R.string.custom_intake_hint) + " " + unit
        }


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

            val currentTarget = sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
            weight = binding.etWeight.editText!!.text.toString()
            workTime = binding.etWorkTime.editText!!.text.toString()
            customTarget = binding.etTarget.editText!!.text.toString()

            val editor = sharedPref.edit()
            val sqliteHelper = SqliteHelper(requireContext())

            if(weight != oldWeight.toString() ||
                workTime != oldWorkTime.toString()){
                val totalIntake = AppUtils.calculateIntake(
                    weight.toInt(),
                    workTime.toInt(),
                    weightUnit
                )
                val df = DecimalFormat("#")
                df.roundingMode = RoundingMode.CEILING
                editor.putFloat(AppUtils.TOTAL_INTAKE_KEY, df.format(totalIntake).toFloat())

                editor.apply()

                sqliteHelper.updateTotalIntake(
                    AppUtils.getCurrentOnlyDate()!!,
                    df.format(totalIntake).toFloat(), calculateExtensions(newUnitint)
                )
            }
            when {
                TextUtils.isEmpty(weight) -> showError(getString(R.string.please_input_your_weight))

                weight.toInt() > AppUtils.getMaxWeight(weightUnit) || weight.toInt() < AppUtils.getMinWeight(weightUnit) ->
                    showError(getString(R.string.please_input_a_valid_weight))

                TextUtils.isEmpty(workTime) -> showError(
                    getString(R.string.please_input_your_workout_time)
                )

                workTime.toInt() > 500 || workTime.toInt() < 0 ->
                    showError(getString(R.string.please_input_a_valid_workout_time))

                TextUtils.isEmpty(customTarget) -> showError(getString(R.string.please_input_your_custom_target))
                !AppUtils.isValidDate(binding.etSleepTime.editText!!.text.toString(),
                    binding.etWakeUpTime.editText!!.text.toString()) ->
                    showError(getString(R.string.please_input_a_valid_rest_time))
                else -> {

                    editor.putInt(AppUtils.WEIGHT_KEY, weight.toInt())
                    editor.putInt(AppUtils.WORK_TIME_KEY, workTime.toInt())
                    editor.putLong(AppUtils.WAKEUP_TIME_KEY, wakeupTime)
                    editor.putLong(AppUtils.SLEEPING_TIME_KEY, sleepingTime)

                    if (currentTarget != customTarget.toFloat()) {
                        editor.putFloat(AppUtils.TOTAL_INTAKE_KEY, customTarget.toFloat())

                        sqliteHelper.updateTotalIntake(
                            AppUtils.getCurrentOnlyDate()!!,
                            customTarget.toFloat(), calculateExtensions(newUnitint)
                        )
                    } else {
                        val totalIntake = AppUtils.calculateIntake(
                            weight.toInt(),
                            workTime.toInt(),
                            weightUnit
                        )
                        val df = DecimalFormat("#")
                        df.roundingMode = RoundingMode.CEILING
                        editor.putFloat(AppUtils.TOTAL_INTAKE_KEY, df.format(totalIntake).toFloat())

                        sqliteHelper.updateTotalIntake(
                            AppUtils.getCurrentOnlyDate()!!,
                            df.format(totalIntake).toFloat(), calculateExtensions(newUnitint)
                        )
                    }

                    editor.putBoolean(NO_UPDATE_UNIT,true)

                    editor.apply()

                    Toast.makeText(requireContext(), getString(rpt.tool.mementobibere.R.string.values_updated_successfully), Toast.LENGTH_SHORT).show()
                    var alarm = AlarmHelper()
                    alarm.cancelAlarm(requireContext())
                    dismiss()
                    requireActivity().finish()
                    requireActivity().overridePendingTransition(0, 0);
                    startActivity(requireActivity().intent);
                    requireActivity().overridePendingTransition(0, 0);
                }
            }
        }

        if(Locale.getDefault().language == "de"){
            binding.etWeight.editText!!.hint = requireContext().getString(R.string.weight_hint) + " " + AppUtils.calculateExtensionsForWeight(weightUnit,requireContext()) +  " ein"
        }
        else{
            binding.etWeight.editText!!.hint = requireContext().getString(R.string.weight_hint) + " " + AppUtils.calculateExtensionsForWeight(weightUnit,requireContext())
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
        }
    }

    private fun toDarkTheme() {
        setBackgroundColor(requireContext().getColor(R.color.darkGreen))
        binding.textView7.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.btnUpdate.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.etWeight.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etWorkTime.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etWakeUpTime.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etSleepTime.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etTarget.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etWeight.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etWorkTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etWakeUpTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etSleepTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.etTarget.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))

    }

    private fun toLightTheme() {
        setBackgroundColor(requireContext().getColor(R.color.colorSecondaryDark))
        binding.textView7.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.btnUpdate.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.etWeight.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etWorkTime.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etWakeUpTime.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etSleepTime.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etTarget.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etWeight.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etWorkTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etWakeUpTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etSleepTime.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.etTarget.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
    }

    private fun setBackgroundColor(color: Int) {
        binding.bottomSheetParent.setBackgroundColor(color)
    }
}