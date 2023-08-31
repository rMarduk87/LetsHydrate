package rpt.tool.mementobibere.ui.fragments.userinfo

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.EditInfoBottomSheetFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.AlarmHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


class EditInfoBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var binding: EditInfoBottomSheetFragmentBinding
    private var weight: String = ""
    private var workTime: String = ""
    private var customTarget: String = ""
    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
    private var notificMsg: String = ""
    private var notificFrequency: Int = 45
    private var currentToneUri: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EditInfoBottomSheetFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val is24h = android.text.format.DateFormat.is24HourFormat(requireContext())

        sharedPref = requireContext().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

        binding.etWeight.editText!!.setText("" + sharedPref.getInt(AppUtils.WEIGHT_KEY, 0))
        binding.etWorkTime.editText!!.setText("" + sharedPref.getInt(AppUtils.WORK_TIME_KEY, 0))
        binding.etTarget.editText!!.setText("" + sharedPref.getInt(AppUtils.TOTAL_INTAKE, 0))
        binding.etNotificationText.editText!!.setText(
            sharedPref.getString(
                AppUtils.NOTIFICATION_MSG_KEY,
                getString(R.string.hey_lets_drink_some_water)
            )
        )
        currentToneUri = sharedPref.getString(
            AppUtils.NOTIFICATION_TONE_URI_KEY,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()
        )
        binding.etRingtone.editText!!.setText(
            RingtoneManager.getRingtone(
                requireContext(),
                Uri.parse(currentToneUri)
            ).getTitle(requireContext())
        )


        binding.etRingtone.editText!!.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                getString(R.string.select_ringtone_for_notifications)
            )
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentToneUri)
            startActivityForResult(intent, 999)
        }

        wakeupTime = sharedPref.getLong(AppUtils.WAKEUP_TIME, 1558323000000)
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

        binding.btnUpdate.setOnClickListener {

            val currentTarget = sharedPref.getInt(AppUtils.TOTAL_INTAKE, 0)

            weight = binding.etWeight.editText!!.text.toString()
            workTime = binding.etWorkTime.editText!!.text.toString()
            notificMsg = binding.etNotificationText.editText!!.text.toString()
            customTarget = binding.etTarget.editText!!.text.toString()

            when {
                TextUtils.isEmpty(notificMsg) -> showError(getString(R.string.please_a_notification_message), it)
                notificFrequency == 0 -> showError(getString(R.string.please_select_a_notification_frequency),it)
                TextUtils.isEmpty(weight) -> showError(getString(R.string.please_input_your_weight),it)

                weight.toInt() > 200 || weight.toInt() < 20 ->
                    showError(getString(R.string.please_input_a_valid_weight), it)

                TextUtils.isEmpty(workTime) -> showError(
                    getString(R.string.please_input_your_workout_time),
                    it
                )

                workTime.toInt() > 500 || workTime.toInt() < 0 ->
                    showError(getString(R.string.please_input_a_valid_workout_time), it)

                TextUtils.isEmpty(customTarget) -> showError(getString(R.string.please_input_your_custom_target),it)
                else -> {

                    val editor = sharedPref.edit()
                    editor.putInt(AppUtils.WEIGHT_KEY, weight.toInt())
                    editor.putInt(AppUtils.WORK_TIME_KEY, workTime.toInt())
                    editor.putLong(AppUtils.WAKEUP_TIME, wakeupTime)
                    editor.putLong(AppUtils.SLEEPING_TIME_KEY, sleepingTime)
                    editor.putString(AppUtils.NOTIFICATION_MSG_KEY, notificMsg)
                    editor.putInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, notificFrequency)

                    val sqliteHelper = SqliteHelper(requireContext())

                    if (currentTarget != customTarget.toInt()) {
                        editor.putInt(AppUtils.TOTAL_INTAKE, customTarget.toInt())

                        sqliteHelper.updateTotalIntake(
                            AppUtils.getCurrentDate()!!,
                            customTarget.toInt()
                        )
                    } else {
                        val totalIntake = AppUtils.calculateIntake(weight.toInt(), workTime.toInt())
                        val df = DecimalFormat("#")
                        df.roundingMode = RoundingMode.CEILING
                        editor.putInt(AppUtils.TOTAL_INTAKE, df.format(totalIntake).toInt())

                        sqliteHelper.updateTotalIntake(
                            AppUtils.getCurrentDate()!!,
                            df.format(totalIntake).toInt()
                        )
                    }

                    editor.apply()

                    Toast.makeText(requireContext(), getString(R.string.values_updated_successfully), Toast.LENGTH_SHORT).show()
                    val alarmHelper = AlarmHelper()
                    alarmHelper.cancelAlarm(requireContext())
                    alarmHelper.setAlarm(
                        requireContext(),
                        sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
                    )
                    dismiss()
                    (activity as MainActivity?)!!.updateValues()

                }
            }
        }
    }

    private fun showError(error: String, view: View?) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 999) {

            val uri = data!!.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI) as Uri
            currentToneUri = uri.toString()
            sharedPref.edit().putString(AppUtils.NOTIFICATION_TONE_URI_KEY, currentToneUri).apply()
            val ringtone = RingtoneManager.getRingtone(requireContext(), uri)
            binding.etRingtone.editText!!.setText(ringtone.getTitle(requireContext()))

        }
    }
}