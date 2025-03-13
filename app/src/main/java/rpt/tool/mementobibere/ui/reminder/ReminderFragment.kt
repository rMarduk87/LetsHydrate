package rpt.tool.mementobibere.ui.reminder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.wdullaer.materialdatetimepicker.time.Timepoint
import android.media.MediaPlayer
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentReminderBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.data.appmodel.AlarmModel
import rpt.tool.mementobibere.utils.data.appmodel.IntervalModel
import rpt.tool.mementobibere.utils.data.appmodel.SoundModel
import rpt.tool.mementobibere.utils.helpers.AlertHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.managers.MyAlarmManager
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.view.adapters.AlarmAdapter
import rpt.tool.mementobibere.utils.view.adapters.IntervalAdapter
import rpt.tool.mementobibere.utils.view.adapters.SoundAdapter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
class ReminderFragment : BaseFragment<FragmentReminderBinding>(FragmentReminderBinding::inflate) {

    var alarmModelList: MutableList<AlarmModel> = ArrayList()
    var alarmAdapter: AlarmAdapter? = null
    var bottomSheetDialogSound: BottomSheetDialog? = null
    var from_hour: Int = 0
    var from_minute: Int = 0
    var to_hour: Int = 0
    var to_minute: Int = 0
    var interval: Int = 30
    var lst_interval: MutableList<String> = ArrayList()
    var lst_sounds: MutableList<SoundModel> = ArrayList()
    var soundAdapter: SoundAdapter? = null
    var intervalAdapter: IntervalAdapter? = null
    var lst_intervals: MutableList<IntervalModel> = ArrayList()
    var sqliteHelper: SqliteHelper? = null
    var alertHelper: AlertHelper? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.navigationBarColor =
            requireContext().resources.getColor(R.color.str_green_card)
        interval = SharedPreferencesManager.notificationFreq.toInt()
        sqliteHelper = SqliteHelper(requireContext())
        alertHelper = AlertHelper(requireContext())
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

    private fun setAutoAlarmAndRemoveAllManualAlarm() {
        for (k in alarmModelList.indices) {
            val time = alarmModelList[k]

            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmSundayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmMondayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmTuesdayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmWednesdayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmThursdayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmFridayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmSaturdayId!!.toInt())
        }

        setAutoAlarm(false)
    }

    private fun setAllManualAlarmAndRemoveAutoAlarm() {
        val arr_data: ArrayList<HashMap<String, String>> =
            sqliteHelper!!.getdata("alarm", "alarm_type='R'")

        for (k in arr_data.indices) {
            MyAlarmManager.cancelRecurringAlarm(requireContext(), arr_data[k]["alarm_id"]!!.toInt())

            val arr_data2: ArrayList<HashMap<String, String>> =
                sqliteHelper!!.getdata("sub_alarm", "super_id=" + arr_data[k]["id"])
            for (j in arr_data2.indices) MyAlarmManager.cancelRecurringAlarm(
                requireContext(), arr_data2[j]["alarm_id"]!!
                    .toInt()
            )
        }

        for (k in alarmModelList.indices) {
            val time = alarmModelList[k]

            val hourOfDay = ("" +
                    AppUtils.FormateDateFromString(
                        "hh:mm a",
                        "HH",
                        time.drinkTime!!.trim { it <= ' ' })).toInt()
            val minute = ("" +
                    AppUtils.FormateDateFromString(
                        "hh:mm a",
                        "mm",
                        time.drinkTime!!.trim { it <= ' ' })).toInt()

            d("setAllManualAlarm : ", "" + time.sunday)

            if (time.sunday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                requireContext(), Calendar.SUNDAY, hourOfDay,
                minute, ("" + time.alarmSundayId).toInt()
            )
            if (time.monday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                requireContext(), Calendar.MONDAY, hourOfDay,
                minute, ("" + time.alarmMondayId).toInt()
            )
            if (time.tuesday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                requireContext(), Calendar.TUESDAY, hourOfDay,
                minute, ("" + time.alarmTuesdayId).toInt()
            )
            if (time.wednesday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                requireContext(), Calendar.WEDNESDAY, hourOfDay,
                minute, ("" + time.alarmWednesdayId).toInt()
            )
            if (time.thursday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                requireContext(), Calendar.THURSDAY, hourOfDay,
                minute, ("" + time.alarmThursdayId).toInt()
            )
            if (time.friday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                requireContext(), Calendar.FRIDAY, hourOfDay,
                minute, ("" + time.alarmFridayId).toInt()
            )
            if (time.saturday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                requireContext(), Calendar.SATURDAY, hourOfDay,
                minute, ("" + time.alarmSaturdayId).toInt()
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun load_AutoDataFromDB() {
        val arr_data: ArrayList<HashMap<String, String>> =
            sqliteHelper!!.getdata("alarm", "alarm_type='R'")

        if (arr_data.size > 0) {
            val str_date =
                arr_data[0]["alarm_time"]!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()

            if (str_date.size > 1) {
                binding.lblWakeupTime.text = str_date[0].trim { it <= ' ' }
                binding.lblBedTime.text = str_date[1].trim { it <= ' ' }
            }

            from_hour = ("" + AppUtils.FormateDateFromString(
                "hh:mm a",
                "HH",
                str_date[0].trim { it <= ' ' })).toInt()
            from_minute = ("" + AppUtils.FormateDateFromString(
                "hh:mm a",
                "mm",
                str_date[0].trim { it <= ' ' })).toInt()

            to_hour = ("" + AppUtils.FormateDateFromString(
                "hh:mm a",
                "HH",
                str_date[1].trim { it <= ' ' })).toInt()
            to_minute = ("" + AppUtils.FormateDateFromString(
                "hh:mm a",
                "mm",
                str_date[1].trim { it <= ' ' })).toInt()

            interval = ("" + arr_data[0]["alarm_interval"]).toFloat().toInt()

            if (arr_data[0]["alarm_interval"].equals(
                    "60",
                    ignoreCase = true
                )
            ) binding.lblInterval.text = "1 " + requireContext().getString(R.string.str_hour)
            else binding.lblInterval.text = arr_data[0]["alarm_interval"] + " " + requireContext().getString(R.string.str_min)
        }
    }

    private fun body() {


        load_AutoDataFromDB()
        
        binding.lblWakeupTime.setOnClickListener {
            openAutoTimePicker2(
                binding.lblWakeupTime,
                true
            )
        }

        binding.lblBedTime.setOnClickListener {
            openAutoTimePicker2(
                binding.lblBedTime,
                false
            )
        }

        binding.lblInterval.setOnClickListener { openIntervalPicker() }


        if (SharedPreferencesManager.isManualReminder) {
            binding.rdoManualAlarm.isChecked = true
            binding.manualReminderBlock.visibility = View.VISIBLE
            binding.autoReminderBlock.visibility = View.GONE
        } else {
            binding.rdoAutoAlarm.isChecked = true
            binding.manualReminderBlock.visibility = View.GONE
            binding.autoReminderBlock.visibility = View.VISIBLE
        }


        binding.rdoAutoAlarm.setOnClickListener {
            binding.manualReminderBlock.visibility = View.GONE
            binding.autoReminderBlock.visibility = View.VISIBLE
            SharedPreferencesManager.isManualReminder = false
        }

        binding.rdoManualAlarm.setOnClickListener {
            binding.manualReminderBlock.visibility = View.VISIBLE
            binding.autoReminderBlock.visibility = View.GONE
            SharedPreferencesManager.isManualReminder = true
            if (alarmModelList.size > 0) binding.lblNoRecordFound.visibility = View.GONE
            else binding.lblNoRecordFound.visibility = View.VISIBLE
        }

        binding.rdoAutoAlarm.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setAutoAlarmAndRemoveAllManualAlarm()
            }
        }

        binding.rdoManualAlarm.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setAllManualAlarmAndRemoveAutoAlarm()
            }
        }

        binding.saveReminder.setOnClickListener {
            if (isValidDate()) {
                setAutoAlarm(true)
            } else {
                alertHelper!!.customAlert(requireContext().getString(R.string.str_from_to_invalid_validation))
            }
        }
        
        binding.addReminder.setOnClickListener { openTimePicker() }

        lst_interval.clear()
        lst_interval.add("30 " + requireContext().getString(R.string.str_minutes))
        lst_interval.add("45 " + requireContext().getString(R.string.str_minutes))
        lst_interval.add("60 " + requireContext().getString(R.string.str_minutes))
        lst_interval.add("90 " + requireContext().getString(R.string.str_minutes))
        lst_interval.add("120 " + requireContext().getString(R.string.str_minutes))
        
        binding.include1.leftIconBlock.setOnClickListener { finish() }
        binding.include1.rightIconBlock.setOnClickListener { view ->
            showMenu(view)
        }

        load_alarm()

        binding.switchVibrate.setChecked(!SharedPreferencesManager.reminderVibrate)

        binding.switchVibrate.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferencesManager.reminderVibrate = !isChecked
        }

        alarmAdapter = AlarmAdapter(requireActivity(), alarmModelList, object : AlarmAdapter.CallBack {
            override fun onClickSelect(time: AlarmModel?, position: Int) {
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onClickRemove(time: AlarmModel?, position: Int) {
                val dialog: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
                    .setMessage(requireContext().getString(R.string.str_reminder_remove_confirm_message))
                    .setPositiveButton(
                        requireContext().getString(R.string.str_yes)
                    ) { dialog, whichButton ->
                        MyAlarmManager.cancelRecurringAlarm(
                            requireContext(),
                            time!!.alarmSundayId!!.toInt()
                        )
                        MyAlarmManager.cancelRecurringAlarm(
                            requireContext(),
                            time.alarmMondayId!!.toInt()
                        )
                        MyAlarmManager.cancelRecurringAlarm(
                            requireContext(),
                            time.alarmTuesdayId!!.toInt()
                        )
                        MyAlarmManager.cancelRecurringAlarm(
                            requireContext(),
                            time.alarmWednesdayId!!.toInt()
                        )
                        MyAlarmManager.cancelRecurringAlarm(
                            requireContext(),
                            time.alarmThursdayId!!.toInt()
                        )
                        MyAlarmManager.cancelRecurringAlarm(
                            requireContext(),
                            time.alarmFridayId!!.toInt()
                        )
                        MyAlarmManager.cancelRecurringAlarm(
                            requireContext(),
                            time.alarmSaturdayId!!.toInt()
                        )

                        alarmModelList.removeAt(position)
                        sqliteHelper!!.remove("alarm", "id=" + time.id)

                        alarmAdapter!!.notifyDataSetChanged()

                        if (alarmModelList.size > 0) binding.lblNoRecordFound.visibility = View.GONE
                        else binding.lblNoRecordFound.visibility = View.VISIBLE
                        dialog.dismiss()
                    }
                    .setNegativeButton(
                        requireContext().getString(R.string.str_no)
                    ) { dialog, whichButton -> dialog.dismiss() }

                dialog.show()
            }

            override fun onClickEdit(time: AlarmModel?, position: Int) {

                if (time!!.isOff != 1) openEditTimePicker(time)
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onClickSwitch(time: AlarmModel?, position: Int, isOn: Boolean) {
                val initialValues = ContentValues()
                initialValues.put("is_off", if (isOn) 0 else 1)
                sqliteHelper!!.update("alarm", initialValues, "id=" + time!!.id)

                alarmModelList[position].isOff = if (isOn) 0 else 1

                if (isOn) {
                    val hourOfDay = ("" +
                            AppUtils.FormateDateFromString(
                                "hh:mm a",
                                "HH",
                                time.drinkTime!!.trim { it <= ' ' })).toInt()
                    val minute = ("" +
                            AppUtils.FormateDateFromString(
                                "hh:mm a",
                                "mm",
                                time.drinkTime!!.trim { it <= ' ' })).toInt()
                    
                    if (time.sunday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                        requireContext(), Calendar.SUNDAY, hourOfDay,
                        minute, ("" + time.alarmSundayId).toInt()
                    )
                    if (time.monday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                        requireContext(), Calendar.MONDAY, hourOfDay,
                        minute, ("" + time.alarmMondayId).toInt()
                    )
                    if (time.tuesday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                        requireContext(), Calendar.TUESDAY, hourOfDay,
                        minute, ("" + time.alarmTuesdayId).toInt()
                    )
                    if (time.wednesday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                        requireContext(), Calendar.WEDNESDAY, hourOfDay,
                        minute, ("" + time.alarmWednesdayId).toInt()
                    )
                    if (time.thursday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                        requireContext(), Calendar.THURSDAY, hourOfDay,
                        minute, ("" + time.alarmThursdayId).toInt()
                    )
                    if (time.friday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                        requireContext(), Calendar.FRIDAY, hourOfDay,
                        minute, ("" + time.alarmFridayId).toInt()
                    )
                    if (time.saturday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                        requireContext(), Calendar.SATURDAY, hourOfDay,
                        minute, ("" + time.alarmSaturdayId).toInt()
                    )

                    if (alarmModelList[position].sunday == 0 && alarmModelList[position].monday == 0
                        && alarmModelList[position].tuesday == 0 
                        && alarmModelList[position].thursday == 0 &&
                        alarmModelList[position].wednesday == 0 &&
                        alarmModelList[position].friday == 0 && 
                        alarmModelList[position].saturday == 0) {
                        val tmp_from_hour = ("" +
                                AppUtils.FormateDateFromString(
                                    "hh:mm a",
                                    "HH",
                                    time.drinkTime!!.trim { it <= ' ' })).toInt()
                        val tmp_from_minute = ("" +
                                AppUtils.FormateDateFromString(
                                    "hh:mm a",
                                    "mm",
                                    time.drinkTime!!.trim { it <= ' ' })).toInt()


                        val date = Calendar.getInstance(Locale.getDefault())
                        val week_pos = date[Calendar.DAY_OF_WEEK]

                        val initialValues4 = ContentValues()

                        if (week_pos == 1) {
                            initialValues4.put("sunday", 1)
                            alarmModelList[position].sunday = 1

                            val _id = ("" + time.alarmSundayId).toInt()

                            MyAlarmManager.scheduleManualRecurringAlarm(
                                requireContext(),
                                Calendar.SUNDAY, tmp_from_hour, tmp_from_minute, _id
                            )
                        } else if (week_pos == 2) {
                            initialValues4.put("monday", 1)
                            alarmModelList[position].monday = 1

                            val _id = ("" + time.alarmMondayId).toInt()

                            MyAlarmManager.scheduleManualRecurringAlarm(
                                requireContext(),
                                Calendar.MONDAY, tmp_from_hour, tmp_from_minute, _id
                            )
                        } else if (week_pos == 3) {
                            initialValues4.put("tuesday", 1)
                            alarmModelList[position].tuesday = 1

                            val _id = ("" + time.alarmTuesdayId).toInt()

                            MyAlarmManager.scheduleManualRecurringAlarm(
                                requireContext(),
                                Calendar.TUESDAY, tmp_from_hour, tmp_from_minute, _id
                            )
                        } else if (week_pos == 4) {
                            initialValues4.put("wednesday", 1)
                            alarmModelList[position].wednesday = 1

                            val _id = ("" + time.alarmWednesdayId).toInt()

                            MyAlarmManager.scheduleManualRecurringAlarm(
                                requireContext(),
                                Calendar.WEDNESDAY, tmp_from_hour, tmp_from_minute, _id
                            )
                        } else if (week_pos == 5) {
                            initialValues4.put("thursday", 1)
                            alarmModelList[position].thursday = 1

                            val _id = ("" + time.alarmThursdayId).toInt()

                            MyAlarmManager.scheduleManualRecurringAlarm(
                                requireContext(),
                                Calendar.THURSDAY, tmp_from_hour, tmp_from_minute, _id
                            )
                        } else if (week_pos == 6) {
                            initialValues4.put("friday", 1)
                            alarmModelList[position].friday = 1

                            val _id = ("" + time.alarmFridayId).toInt()

                            MyAlarmManager.scheduleManualRecurringAlarm(
                                requireContext(),
                                Calendar.FRIDAY, tmp_from_hour, tmp_from_minute, _id
                            )
                        } else if (week_pos == 7) {
                            initialValues4.put("saturday", 1)
                            alarmModelList[position].saturday = 1

                            val _id = ("" + time.alarmSaturdayId).toInt()

                            MyAlarmManager.scheduleManualRecurringAlarm(
                                requireContext(),
                                Calendar.SATURDAY, tmp_from_hour, tmp_from_minute, _id
                            )
                        }
                        
                        binding.alarmRecyclerView.post { alarmAdapter!!.notifyDataSetChanged() }

                    }
                } else {
                    MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmSundayId!!.toInt())
                    MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmMondayId!!.toInt())
                    MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmTuesdayId!!.toInt())
                    MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmWednesdayId!!.toInt())
                    MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmThursdayId!!.toInt())
                    MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmFridayId!!.toInt())
                    MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmSaturdayId!!.toInt())
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onClickWeek(
                time: AlarmModel?,
                position: Int,
                week_pos: Int,
                isOn: Boolean
            ) {
                val tmp_from_hour = ("" +
                        AppUtils.FormateDateFromString(
                            "hh:mm a",
                            "HH",
                            time!!.drinkTime!!.trim { it <= ' ' })).toInt()
                val tmp_from_minute = ("" +
                        AppUtils.FormateDateFromString(
                            "hh:mm a",
                            "mm",
                            time.drinkTime!!.trim { it <= ' ' })).toInt()
                val initialValues = ContentValues()

                if (isOn) {
                    initialValues.put("is_off", "0")
                    alarmModelList[position].isOff = if (isOn) 0 else 1
                }

                if (week_pos == 0) {
                    initialValues.put("sunday", if (isOn) 1 else 0)
                    alarmModelList[position].sunday = if (isOn) 1 else 0

                    val _id = ("" + time.alarmSundayId).toInt()

                    if (isOn) {
                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.SUNDAY, tmp_from_hour, tmp_from_minute, _id
                        )
                    } else {
                        MyAlarmManager.cancelRecurringAlarm(requireContext(), _id)
                    }
                } else if (week_pos == 1) {
                    initialValues.put("monday", if (isOn) 1 else 0)
                    alarmModelList[position].monday = if (isOn) 1 else 0

                    val _id = ("" + time.alarmMondayId).toInt()

                    if (isOn) {
                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.MONDAY, tmp_from_hour, tmp_from_minute, _id
                        )
                    } else {
                        MyAlarmManager.cancelRecurringAlarm(requireContext(), _id)
                    }
                } else if (week_pos == 2) {
                    initialValues.put("tuesday", if (isOn) 1 else 0)
                    alarmModelList[position].tuesday = if (isOn) 1 else 0

                    val _id = ("" + time.alarmTuesdayId).toInt()

                    if (isOn) {
                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.TUESDAY, tmp_from_hour, tmp_from_minute, _id
                        )
                    } else {
                        MyAlarmManager.cancelRecurringAlarm(requireContext(), _id)
                    }
                } else if (week_pos == 3) {
                    initialValues.put("wednesday", if (isOn) 1 else 0)
                    alarmModelList[position].wednesday = if (isOn) 1 else 0

                    val _id = ("" + time.alarmWednesdayId).toInt()

                    if (isOn) {
                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.WEDNESDAY, tmp_from_hour, tmp_from_minute, _id
                        )
                    } else {
                        MyAlarmManager.cancelRecurringAlarm(requireContext(), _id)
                    }
                } else if (week_pos == 4) {
                    initialValues.put("thursday", if (isOn) 1 else 0)
                    alarmModelList[position].thursday = if (isOn) 1 else 0

                    val _id = ("" + time.alarmThursdayId).toInt()

                    if (isOn) {
                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.THURSDAY, tmp_from_hour, tmp_from_minute, _id
                        )
                    } else {
                        MyAlarmManager.cancelRecurringAlarm(requireContext(), _id)
                    }
                } else if (week_pos == 5) {
                    initialValues.put("friday", if (isOn) 1 else 0)
                    alarmModelList[position].friday = if (isOn) 1 else 0

                    val _id = ("" + time.alarmFridayId).toInt()

                    if (isOn) {
                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.FRIDAY, tmp_from_hour, tmp_from_minute, _id
                        )
                    } else {
                        MyAlarmManager.cancelRecurringAlarm(requireContext(), _id)
                    }
                } else if (week_pos == 6) {
                    initialValues.put("saturday", if (isOn) 1 else 0)
                    alarmModelList[position].saturday = if (isOn) 1 else 0

                    val _id = ("" + time.alarmSaturdayId).toInt()

                    if (isOn) {
                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.SATURDAY, tmp_from_hour, tmp_from_minute, _id
                        )
                    } else {
                        MyAlarmManager.cancelRecurringAlarm(requireContext(), _id)
                    }
                }
                sqliteHelper!!.update("alarm", initialValues, "id=" + time.id)

                if (alarmModelList[position].sunday == 0 && alarmModelList[position].monday == 0
                    && alarmModelList[position].tuesday == 0 && 
                    alarmModelList[position].thursday == 0 && 
                    alarmModelList[position].wednesday == 0 &&
                    alarmModelList[position].friday == 0 && 
                    alarmModelList[position].saturday == 0) {
                    val initialValues2 = ContentValues()
                    initialValues2.put("is_off", "1")
                    sqliteHelper!!.update("alarm", initialValues2, "id=" + time.id)
                    alarmModelList[position].isOff = 1
                }

                alarmAdapter!!.notifyDataSetChanged()
            }
        })

        binding.alarmRecyclerView.setLayoutManager(
            LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.VERTICAL,
                false
            )
        )

        binding.alarmRecyclerView.setAdapter(alarmAdapter)

        if (SharedPreferencesManager.reminderOpt == 1) {
            binding.rdoOff.isChecked = true
        } else if (SharedPreferencesManager.reminderOpt == 2) {
            binding.rdoSilent.isChecked = true
        } else {
            binding.rdoAuto.isChecked = true
        }


        binding.rdoAuto.setOnClickListener {
            SharedPreferencesManager.reminderOpt = 0
        }

        binding.rdoOff.setOnClickListener {
            SharedPreferencesManager.reminderOpt = 1
        }

        binding.rdoSilent.setOnClickListener {
            SharedPreferencesManager.reminderOpt = 2
        }

        loadSounds()

        binding.soundBlock.setOnClickListener { openSoundMenuPicker() }
    }

    private fun finish() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
    }

    private fun showMenu(v: View?) {
        val popup = PopupMenu(requireContext(), v)
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.delete_item -> {
                        // do your code
                        val dialog: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
                            .setMessage(requireContext().getString(R.string.str_reminder_remove_all_confirm_message))
                            .setPositiveButton(
                                requireContext().getString(R.string.str_yes)
                            ) { dialog, whichButton ->
                                deleteAllManualAlarm(true)
                                binding.rdoAutoAlarm.isChecked = true
                                dialog.dismiss()
                            }
                            .setNegativeButton(
                                requireContext().getString(R.string.str_no)
                            ) { dialog, whichButton -> dialog.dismiss() }

                        dialog.show()



                        return true
                    }

                    else -> return false
                }
            }
        })
        popup.inflate(R.menu.delete_all_menu)
        popup.show()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun deleteAllManualAlarm(alsoData: Boolean) // @@@@
    {
        for (k in alarmModelList.indices) {
            val time = alarmModelList[k]
            
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmSundayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmMondayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmTuesdayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmWednesdayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmThursdayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmFridayId!!.toInt())
            MyAlarmManager.cancelRecurringAlarm(requireContext(), time.alarmSaturdayId!!.toInt())

            if (alsoData) sqliteHelper!!.remove("alarm", "id=" + time.id)
        }

        if (alsoData) {
            alarmModelList.clear()
            alarmAdapter!!.notifyDataSetChanged()
        }

        SharedPreferencesManager.isManualReminder = false

        binding.manualReminderBlock.visibility = View.GONE
        binding.autoReminderBlock.visibility = View.VISIBLE

        setAutoAlarm(false)
    }


    private fun loadInterval() {
        lst_intervals.clear()

        lst_intervals.add(getIntervalModel(15, "15 " + requireContext().getString(R.string.str_min)))
        lst_intervals.add(getIntervalModel(30, "30 " + requireContext().getString(R.string.str_min)))
        lst_intervals.add(getIntervalModel(45, "45 " + requireContext().getString(R.string.str_min)))
        lst_intervals.add(getIntervalModel(60, "1 " + requireContext().getString(R.string.str_hour)))
    }

    private fun getIntervalModel(index: Int, name: String?): IntervalModel {
        val intervalModel = IntervalModel()
        intervalModel.id = index
        intervalModel.name = name
        intervalModel.isSelected(interval == index)

        return intervalModel
    }


    @SuppressLint("InflateParams")
    private fun openIntervalPicker() {
        loadInterval()

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val view: View = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_pick_interval,
            null, false)


        val btn_cancel = view.findViewById<RelativeLayout>(R.id.btn_cancel)
        val btn_save = view.findViewById<RelativeLayout>(R.id.btn_save)


        val intervalRecyclerView = view.findViewById<RecyclerView>(R.id.intervalRecyclerView)

        intervalAdapter = IntervalAdapter(requireActivity(), lst_intervals, object : IntervalAdapter.CallBack {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClickSelect(time: IntervalModel?, position: Int) {
                for (k in lst_intervals.indices) {
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
            for (k in lst_intervals.indices) {
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

    @SuppressLint("SetTextI18n")
    private fun openAutoTimePicker2(appCompatTextView: AppCompatTextView, isFrom: Boolean) {
        val onTimeSetListener: TimePickerDialog.OnTimeSetListener =
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, second ->
                var formatedDate = ""
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val sdfs = SimpleDateFormat("hh:mm a", Locale.getDefault())
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

                    d("openAutoTimePicker : ", "$from_hour  @@@@  $from_minute")

                    time = "$hourOfDay:$minute:00"
                    dt = sdf.parse(time)!!
                    formatedDate = sdfs.format(dt)
                    appCompatTextView.text = "" + formatedDate

                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

        val now = Calendar.getInstance(Locale.getDefault())
        if (isFrom) {
            now[Calendar.HOUR_OF_DAY] = from_hour
            now[Calendar.MINUTE] = from_minute
        } else {
            now[Calendar.HOUR_OF_DAY] = to_hour
            now[Calendar.MINUTE] = to_minute
        }
        val tpd: TimePickerDialog
        if (!DateFormat.is24HourFormat(requireActivity())) {
            //12 hrs format
            tpd = TimePickerDialog.newInstance(
                onTimeSetListener,
                now[Calendar.HOUR_OF_DAY],
                now[Calendar.MINUTE], false
            )

            tpd.setSelectableTimes(generateTimepoints(23.50, 30))

            tpd.setMaxTime(23, 30, 0)
        } else {
            //24 hrs format
            tpd = TimePickerDialog.newInstance(
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

    private fun isValidDate(): Boolean {
        val startTime = Calendar.getInstance(Locale.getDefault())
        startTime[Calendar.HOUR_OF_DAY] = from_hour
        startTime[Calendar.MINUTE] = from_minute
        startTime[Calendar.SECOND] = 0

        val endTime = Calendar.getInstance(Locale.getDefault())
        endTime[Calendar.HOUR_OF_DAY] = to_hour
        endTime[Calendar.MINUTE] = to_minute
        endTime[Calendar.SECOND] = 0
        
        if (isNextDayEnd()) endTime.add(Calendar.DATE, 1)

        d("isValidDate", "" + startTime.timeInMillis + " @@@ " + endTime.timeInMillis)

        val mills = endTime.timeInMillis - startTime.timeInMillis
        
        val hours = (mills / (1000 * 60 * 60)).toInt()
        val mins = ((mills / (1000 * 60)) % 60).toInt()
        val total_minutes = ((hours * 60) + mins).toFloat()
        
        if (interval <= total_minutes) return true
        return false
    }

    private fun deleteAutoAlarm(alsoData: Boolean) {
        val arr_data: ArrayList<HashMap<String, String>> =
            sqliteHelper!!.getdata("alarm", "alarm_type='R'")

        for (k in arr_data.indices) {
            MyAlarmManager.cancelRecurringAlarm(requireContext(), arr_data[k]["alarm_id"]!!.toInt())

            val arr_data2: ArrayList<HashMap<String, String>> =
                sqliteHelper!!.getdata("sub_alarm", "super_id=" + arr_data[k]["id"])
            for (j in arr_data2.indices) MyAlarmManager.cancelRecurringAlarm(
                requireContext(), arr_data2[j]["alarm_id"]!!
                    .toInt()
            )

            if (alsoData) {
                sqliteHelper!!.remove("alarm", "id=" + arr_data[k]["id"])
                sqliteHelper!!.remove("sub_alarm", "super_id=" + arr_data[k]["id"])
            }
        }
    }

    private fun isNextDayEnd(): Boolean {
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = simpleDateFormat.parse(binding.lblWakeupTime.getText().toString().trim())
            date2 = simpleDateFormat.parse(binding.lblBedTime.getText().toString().trim())

            return date1.time > date2.time
        } catch (e: Exception) {
            e.message?.let { e(Throwable(e), it) }
        }

        return false
    }

    private fun setAutoAlarm(moveScreen: Boolean) {
        val minute_interval = interval

        if (AppUtils.checkBlankData(binding.lblWakeupTime.getText().toString()) ||
            AppUtils.checkBlankData(binding.lblBedTime.getText().toString())
        ) {
            alertHelper!!.customAlert(requireContext().getString(R.string.str_from_to_invalid_validation))
            return
        } else {
            if (!moveScreen) load_AutoDataFromDB()

            d("setAutoAlarm :", "$from_hour:$from_minute  @@@  $to_hour:$to_minute")

            val startTime = Calendar.getInstance(Locale.getDefault())
            startTime[Calendar.HOUR_OF_DAY] = from_hour
            startTime[Calendar.MINUTE] = from_minute
            startTime[Calendar.SECOND] = 0

            val endTime = Calendar.getInstance(Locale.getDefault())
            endTime[Calendar.HOUR_OF_DAY] = to_hour
            endTime[Calendar.MINUTE] = to_minute
            endTime[Calendar.SECOND] = 0

            if (isNextDayEnd()) endTime.add(Calendar.DATE, 1)

            if (startTime.timeInMillis < endTime.timeInMillis) {
                deleteAutoAlarm(true)

                var _id = System.currentTimeMillis().toInt()

                val initialValues = ContentValues()
                initialValues.put(
                    "alarm_time",
                    "" + binding.lblWakeupTime.getText().toString() + " - " +
                            binding.lblBedTime.getText()
                        .toString()
                )
                initialValues.put("alarm_id", "" + _id)
                initialValues.put("alarm_type", "R")
                initialValues.put("alarm_interval", "" + minute_interval)
                sqliteHelper!!.insert("alarm", initialValues)

                val getLastId: String = sqliteHelper!!.getLastId("alarm")

                while (startTime.timeInMillis <= endTime.timeInMillis) {
                    d(
                        "ALARMTIME  : ",
                        "" + startTime[Calendar.HOUR_OF_DAY] + ":" + startTime[Calendar.MINUTE] + 
                                ":" + startTime[Calendar.SECOND]
                    )

                    try {
                        _id = System.currentTimeMillis().toInt()

                        var formatedDate = ""
                        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        val sdfs = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val dt: Date
                        var time = ""

                        time =
                            startTime[Calendar.HOUR_OF_DAY].toString() + ":" +
                                    startTime[Calendar.MINUTE] + ":" + startTime[Calendar.SECOND]
                        dt = sdf.parse(time)!!
                        formatedDate = sdfs.format(dt)
                        
                        MyAlarmManager.scheduleAutoRecurringAlarm(requireContext(), startTime, _id)

                        val initialValues2 = ContentValues()
                        initialValues2.put("alarm_time", "" + formatedDate)
                        initialValues2.put("alarm_id", "" + _id)
                        initialValues2.put("super_id", "" + getLastId)
                        sqliteHelper!!.insert("sub_alarm", initialValues2)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    startTime.add(Calendar.MINUTE, minute_interval)
                }

                if (moveScreen) {
                    finish()
                }
            } else {
                alertHelper!!.customAlert(requireContext().
                getString(R.string.str_from_to_invalid_validation))
                return
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun openEditTimePicker(alarmtime: AlarmModel?) {
        val tmp_from_hour = ("" +
                AppUtils.FormateDateFromString(
                    "hh:mm a",
                    "HH",
                    alarmtime!!.drinkTime!!.trim { it <= ' ' })).toInt()
        val tmp_from_minute = ("" +
                AppUtils.FormateDateFromString(
                    "hh:mm a",
                    "mm",
                    alarmtime.drinkTime!!.trim { it <= ' ' })).toInt()


        val onTimeSetListener: TimePickerDialog.OnTimeSetListener =
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, second ->
                var formatedDate = ""
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val sdfs = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val dt: Date
                var time = ""

                try {
                    time = "$hourOfDay:$minute:00"
                    dt = sdf.parse(time)!!
                    formatedDate = sdfs.format(dt)
                    if (!sqliteHelper!!.isExists(
                            "alarm",
                            "alarm_time='" + formatedDate + "' AND id<>" + alarmtime.id
                        )
                    ) {

                        val initialValues = ContentValues()
                        initialValues.put("alarm_time", "" + formatedDate)
                        sqliteHelper!!.update("alarm", initialValues,
                            "id=" + alarmtime.id)

                        val _id_sunday = ("" + alarmtime.alarmSundayId).toInt()
                        val _id_monday = ("" + alarmtime.alarmMondayId).toInt()
                        val _id_tuesday = ("" + alarmtime.alarmTuesdayId).toInt()
                        val _id_wednesday = ("" + alarmtime.alarmWednesdayId).toInt()
                        val _id_thursday = ("" + alarmtime.alarmThursdayId).toInt()
                        val _id_friday = ("" + alarmtime.alarmFridayId).toInt()
                        val _id_saturday = ("" + alarmtime.alarmSaturdayId).toInt()


                        if (alarmtime.sunday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.SUNDAY, hourOfDay, minute, _id_sunday
                        )

                        if (alarmtime.monday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.MONDAY, hourOfDay, minute, _id_monday
                        )

                        if (alarmtime.tuesday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.TUESDAY, hourOfDay, minute, _id_tuesday
                        )

                        if (alarmtime.wednesday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.WEDNESDAY, hourOfDay, minute, _id_wednesday
                        )

                        if (alarmtime.thursday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.THURSDAY, hourOfDay, minute, _id_thursday
                        )

                        if (alarmtime.friday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.FRIDAY, hourOfDay, minute, _id_friday
                        )

                        if (alarmtime.saturday == 1) MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.SATURDAY, hourOfDay, minute, _id_saturday
                        )

                        load_alarm()

                        alarmAdapter!!.notifyDataSetChanged()
                    } else {
                        alertHelper!!.customAlert(requireContext()
                            .getString(R.string.str_set_alarm_validation))
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

        val now = Calendar.getInstance(Locale.getDefault())
        now[Calendar.HOUR_OF_DAY] = tmp_from_hour
        now[Calendar.MINUTE] = tmp_from_minute
        val tpd: TimePickerDialog
        if (!DateFormat.is24HourFormat(requireActivity())) {
            //12 hrs format
            tpd = TimePickerDialog.newInstance(
                onTimeSetListener,
                now[Calendar.HOUR_OF_DAY],
                now[Calendar.MINUTE], false
            )

            tpd.setSelectableTimes(generateTimepoints(23.50, 15))

            tpd.setMaxTime(23, 30, 0)
        } else {
            //24 hrs format
            tpd = TimePickerDialog.newInstance(
                onTimeSetListener,
                now[Calendar.HOUR_OF_DAY],
                now[Calendar.MINUTE], true
            )

            tpd.setSelectableTimes(generateTimepoints(23.50, 15))

            tpd.setMaxTime(23, 30, 0)
        }

        tpd.show(requireActivity().fragmentManager, "Datepickerdialog")
        tpd.accentColor = getThemeColor(requireContext())
    }


    private fun loadSounds() {
        lst_sounds.clear()

        lst_sounds.add(getSoundModel(0, "Default"))
        lst_sounds.add(getSoundModel(1, "Bell"))
        lst_sounds.add(getSoundModel(2, "Blop"))
        lst_sounds.add(getSoundModel(3, "Bong"))
        lst_sounds.add(getSoundModel(4, "Click"))
        lst_sounds.add(getSoundModel(5, "Echo droplet"))
        lst_sounds.add(getSoundModel(6, "Mario droplet"))
        lst_sounds.add(getSoundModel(7, "Ship bell"))
        lst_sounds.add(getSoundModel(8, "Simple droplet"))
        lst_sounds.add(getSoundModel(9, "Tiny droplet"))
    }

    private fun getSoundModel(index: Int, name: String?): SoundModel {
        val soundModel = SoundModel()
        soundModel.id = index
        soundModel.name = name
        soundModel.isSelected(SharedPreferencesManager.reminderSound == index)

        return soundModel
    }

    @SuppressLint("InflateParams")
    private fun openSoundMenuPicker() {
        bottomSheetDialogSound = BottomSheetDialog(requireActivity())

        val layoutInflater = LayoutInflater.from(requireActivity())
        val view: View = layoutInflater.inflate(R.layout.dialog_sound_pick, null, false)

        val btnCancel = view.findViewById<AppCompatTextView>(R.id.btn_cancel)
        val soundRecyclerView = view.findViewById<RecyclerView>(R.id.soundRecyclerView)


        soundAdapter = SoundAdapter(requireActivity(), lst_sounds, object : SoundAdapter.CallBack {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClickSelect(time: SoundModel?, position: Int) {
                for (k in lst_sounds.indices) {
                    lst_sounds[k].isSelected(false)
                }

                lst_sounds[position].isSelected(true)
                soundAdapter!!.notifyDataSetChanged()

                SharedPreferencesManager.reminderSound = position

                if (position > 0) playSound(position)
            }
        })


        soundRecyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        soundRecyclerView.adapter = soundAdapter



        btnCancel.setOnClickListener { bottomSheetDialogSound!!.dismiss() }

        bottomSheetDialogSound!!.setContentView(view)

        bottomSheetDialogSound!!.show()
    }

    fun playSound(idx: Int) {
        var mp: MediaPlayer? = null

        if (idx == 1) mp = MediaPlayer.create(requireContext(), R.raw.bell)
        else if (idx == 2) mp = MediaPlayer.create(requireContext(), R.raw.blop)
        else if (idx == 3) mp = MediaPlayer.create(requireContext(), R.raw.bong)
        else if (idx == 4) mp = MediaPlayer.create(requireContext(), R.raw.click)
        else if (idx == 5) mp = MediaPlayer.create(requireContext(), R.raw.echo_droplet)
        else if (idx == 6) mp = MediaPlayer.create(requireContext(), R.raw.mario_droplet)
        else if (idx == 7) mp = MediaPlayer.create(requireContext(), R.raw.ship_bell)
        else if (idx == 8) mp = MediaPlayer.create(requireContext(), R.raw.simple_droplet)
        else if (idx == 9) mp = MediaPlayer.create(requireContext(), R.raw.tiny_droplet)

        mp!!.start()
    }

    fun load_alarm() {
        alarmModelList.clear()

        val arr_data: ArrayList<HashMap<String, String>> =
            sqliteHelper!!.getdata("alarm", "alarm_type='M'")

        for (k in arr_data.indices) {
            val alarmModel = AlarmModel()
            alarmModel.drinkTime = arr_data[k]["alarm_time"]
            alarmModel.id = arr_data[k]["id"]
            alarmModel.alarmId = arr_data[k]["alarm_id"]
            alarmModel.alarmType = arr_data[k]["alarm_type"]
            alarmModel.alarmInterval = arr_data[k]["alarm_interval"]

            alarmModel.isOff = arr_data[k]["is_off"]!!.toInt()
            alarmModel.sunday = arr_data[k]["sunday"]!!.toInt()
            alarmModel.monday = arr_data[k]["monday"]!!.toInt()
            alarmModel.tuesday = arr_data[k]["tuesday"]!!.toInt()
            alarmModel.wednesday = arr_data[k]["wednesday"]!!.toInt()
            alarmModel.thursday = arr_data[k]["thursday"]!!.toInt()
            alarmModel.friday = arr_data[k]["friday"]!!.toInt()
            alarmModel.saturday = arr_data[k]["saturday"]!!.toInt()

            alarmModel.alarmSundayId = arr_data[k]["sunday_alarm_id"]
            alarmModel.alarmMondayId = arr_data[k]["monday_alarm_id"]
            alarmModel.alarmTuesdayId = arr_data[k]["tuesday_alarm_id"]
            alarmModel.alarmWednesdayId = arr_data[k]["wednesday_alarm_id"]
            alarmModel.alarmThursdayId = arr_data[k]["thursday_alarm_id"]
            alarmModel.alarmFridayId = arr_data[k]["friday_alarm_id"]
            alarmModel.alarmSaturdayId = arr_data[k]["saturday_alarm_id"]

            alarmModelList.add(alarmModel)
        }

        if (alarmModelList.size > 0) binding.lblNoRecordFound.visibility = View.GONE
        else binding.lblNoRecordFound.visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun openTimePicker() {
        val onTimeSetListener: TimePickerDialog.OnTimeSetListener =
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, second ->
                var formatedDate = ""
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val sdfs = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val dt: Date
                var time = ""

                try {
                    time = "$hourOfDay:$minute:00"
                    dt = sdf.parse(time)!!
                    formatedDate = sdfs.format(dt)
                    if (!sqliteHelper!!.isExists("alarm", "alarm_time='$formatedDate'")) {
                        val _id = System.currentTimeMillis().toInt()

                        val _id_sunday = System.currentTimeMillis().toInt()

                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.SUNDAY, hourOfDay, minute, _id_sunday
                        )

                        val _id_monday = System.currentTimeMillis().toInt()

                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.MONDAY, hourOfDay, minute, _id_monday
                        )

                        val _id_tuesday = System.currentTimeMillis().toInt()

                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.TUESDAY, hourOfDay, minute, _id_tuesday
                        )

                        val _id_wednesday = System.currentTimeMillis().toInt()

                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.WEDNESDAY, hourOfDay, minute, _id_wednesday
                        )

                        val _id_thursday = System.currentTimeMillis().toInt()

                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.THURSDAY, hourOfDay, minute, _id_thursday
                        )

                        val _id_friday = System.currentTimeMillis().toInt()

                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.FRIDAY, hourOfDay, minute, _id_friday
                        )

                        val _id_saturday = System.currentTimeMillis().toInt()

                        MyAlarmManager.scheduleManualRecurringAlarm(
                            requireContext(),
                            Calendar.SATURDAY, hourOfDay, minute, _id_saturday
                        )


                        val initialValues = ContentValues()
                        initialValues.put("alarm_time", "" + formatedDate)
                        initialValues.put("alarm_id", "" + _id)

                        initialValues.put("sunday_alarm_id", "" + _id_sunday)
                        initialValues.put("monday_alarm_id", "" + _id_monday)
                        initialValues.put("tuesday_alarm_id", "" + _id_tuesday)
                        initialValues.put("wednesday_alarm_id", "" + _id_wednesday)
                        initialValues.put("thursday_alarm_id", "" + _id_thursday)
                        initialValues.put("friday_alarm_id", "" + _id_friday)
                        initialValues.put("saturday_alarm_id", "" + _id_saturday)

                        initialValues.put("alarm_type", "M")
                        initialValues.put("alarm_interval", "0")
                        sqliteHelper!!.insert("alarm", initialValues)

                        load_alarm()

                        alarmAdapter!!.notifyDataSetChanged()
                        alertHelper!!.customAlert(requireContext()
                            .getString(R.string.str_successfully_set_alarm))
                    } else {
                        alertHelper!!.customAlert(requireContext()
                            .getString(R.string.str_set_alarm_validation))
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

        val now = Calendar.getInstance(Locale.getDefault())
        val tpd: TimePickerDialog
        if (!DateFormat.is24HourFormat(requireActivity())) {
            //12 hrs format
            tpd = TimePickerDialog.newInstance(
                onTimeSetListener,
                now[Calendar.HOUR_OF_DAY],
                now[Calendar.MINUTE], false
            )

            tpd.setSelectableTimes(generateTimepoints(23.50, 15))

            tpd.setMaxTime(23, 30, 0)
        } else {
            //24 hrs format
            tpd = TimePickerDialog.newInstance(
                onTimeSetListener,
                now[Calendar.HOUR_OF_DAY],
                now[Calendar.MINUTE], true
            )

            tpd.setSelectableTimes(generateTimepoints(23.50, 15))

            tpd.setMaxTime(23, 30, 0)
        }

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
}