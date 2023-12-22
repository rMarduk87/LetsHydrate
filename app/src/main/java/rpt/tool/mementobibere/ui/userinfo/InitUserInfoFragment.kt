package rpt.tool.mementobibere.ui.userinfo

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.InitUserInfoFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar

@Suppress("DEPRECATION")
class InitUserInfoFragment:
    BaseFragment<InitUserInfoFragmentBinding>(InitUserInfoFragmentBinding::inflate) {

    private var weight: String = ""
    private var workType: Int = 0
    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
    private lateinit var sharedPref: SharedPreferences
    private var unit : Int = 0
    private var weightUnit : Int = 0
    private var gender : Int = 0
    private var bloodDonor : Int = 0
    private var climate : Int = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val is24h = android.text.format.DateFormat.is24HourFormat(requireContext())

        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

        wakeupTime = sharedPref.getLong(AppUtils.WAKEUP_TIME_KEY, 1558323000000)
        sleepingTime = sharedPref.getLong(AppUtils.SLEEPING_TIME_KEY, 1558369800000)

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

        binding.etGender.editText!!.setOnClickListener {

            val li = LayoutInflater.from(requireContext())
            val promptsView = li.inflate(R.layout.custom_input_dialog2, null)

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(promptsView)


            val btnMan = promptsView
                .findViewById(R.id.btnMan) as LottieAnimationView
            val btnWoman = promptsView
                .findViewById(R.id.btnWoman) as LottieAnimationView


            btnMan.setOnClickListener{
                gender = 0
                val editor = sharedPref.edit()
                editor.putInt(AppUtils.GENDER_KEY, gender)
                editor.apply()
                showMessage(getString(R.string.you_selected_man),it,
                    type=AppUtils.Companion.TypeMessage.MAN)
            }

            btnWoman.setOnClickListener{
                gender = 1
                val editor = sharedPref.edit()
                editor.putInt(AppUtils.GENDER_KEY, gender)
                editor.apply()
                showMessage(getString(R.string.you_selected_woman),it,
                    type=AppUtils.Companion.TypeMessage.WOMAN)
            }

            alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                var text = if(gender==0){
                    getString(R.string.man)
                }
                else{
                    getString(R.string.woman)
                }

                binding.etGender.editText!!.setText(text)

            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        binding.btnAvis.setOnClickListener{
            if(bloodDonor==0){
                bloodDonor = 1
                showMessage(getString(R.string.you_selected_avis),it)
            }
            else{
                bloodDonor = 0
                showMessage(getString(R.string.you_selected_no_avis),it)
            }
            val editor = sharedPref.edit()
            editor.putBoolean(AppUtils.SET_BLOOD_KEY, true)
            editor.apply()
        }


        binding.etWorkType.editText!!.setOnClickListener {

            val li = LayoutInflater.from(requireContext())
            val promptsView = li.inflate(R.layout.custom_input_dialog4, null)

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(promptsView)


            val btnCalm = promptsView
                .findViewById(R.id.btnCalm) as LottieAnimationView
            val btnNormal = promptsView
                .findViewById(R.id.btnNormal) as LottieAnimationView
            val btnLively = promptsView
                .findViewById(R.id.btnLively) as LottieAnimationView
            val btnIntense = promptsView
                .findViewById(R.id.btnIntense) as LottieAnimationView


            btnCalm.setOnClickListener{
                workType = 0
                val editor = sharedPref.edit()
                editor.putInt(AppUtils.WORK_TIME_KEY, workType)
                editor.apply()
                showMessage(getString(R.string.you_selected_calm),it,
                    type=AppUtils.Companion.TypeMessage.WORKTYPE,workType = workType)
            }

            btnNormal.setOnClickListener{
                workType = 1
                val editor = sharedPref.edit()
                editor.putInt(AppUtils.WORK_TIME_KEY, workType)
                editor.apply()
                showMessage(getString(R.string.you_selected_normal),it,
                    type=AppUtils.Companion.TypeMessage.WORKTYPE,workType = workType)
            }

            btnLively.setOnClickListener{
                workType = 2
                val editor = sharedPref.edit()
                editor.putInt(AppUtils.WORK_TIME_KEY, workType)
                editor.apply()
                showMessage(getString(R.string.you_selected_lively),it,
                    type=AppUtils.Companion.TypeMessage.WORKTYPE,workType = workType)
            }

            btnIntense.setOnClickListener{
                workType = 3
                val editor = sharedPref.edit()
                editor.putInt(AppUtils.WORK_TIME_KEY, workType)
                editor.apply()
                showMessage(getString(R.string.you_selected_intense),it,
                    type=AppUtils.Companion.TypeMessage.WORKTYPE,workType = workType)
            }

            alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                var text = when(workType){
                    0->getString(R.string.calm)
                    1->getString(R.string.normal)
                    2->getString(R.string.lively)
                    3->getString(R.string.intense)
                    else -> {getString(R.string.calm)}
                }

                binding.etWorkType.editText!!.setText(text)

            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }




            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        binding.etClimate.editText!!.setOnClickListener {

            val li = LayoutInflater.from(requireContext())
            val promptsView = li.inflate(R.layout.custom_input_dialog5, null)

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(promptsView)


            val btnCold = promptsView
                .findViewById(R.id.btnCold) as LottieAnimationView
            val btnFresh = promptsView
                .findViewById(R.id.btnFresh) as LottieAnimationView
            val btnMild = promptsView
                .findViewById(R.id.btnMild) as LottieAnimationView
            val btnTorrid = promptsView
                .findViewById(R.id.btnTorrid) as LottieAnimationView


            btnCold.setOnClickListener{
                climate = 0
                val editor = sharedPref.edit()
                editor.putInt(AppUtils.CLIMATE_KEY, climate)
                editor.apply()
                showMessage(getString(R.string.you_selected_cold),it,
                    type=AppUtils.Companion.TypeMessage.CLIMATE,climate = climate)
            }

            btnFresh.setOnClickListener{
                climate = 1
                val editor = sharedPref.edit()
                editor.putInt(AppUtils.CLIMATE_KEY, climate)
                editor.apply()
                showMessage(getString(R.string.you_selected_fresh),it,
                    type=AppUtils.Companion.TypeMessage.CLIMATE,climate = climate)
            }

            btnMild.setOnClickListener{
                climate = 2
                val editor = sharedPref.edit()
                editor.putInt(AppUtils.CLIMATE_KEY, climate)
                editor.apply()
                showMessage(getString(R.string.you_selected_mild),it,
                    type=AppUtils.Companion.TypeMessage.CLIMATE,climate = climate)
            }

            btnTorrid.setOnClickListener{
                climate = 3
                val editor = sharedPref.edit()
                editor.putInt(AppUtils.CLIMATE_KEY, climate)
                editor.apply()
                showMessage(getString(R.string.you_selected_torrid),it,
                    type=AppUtils.Companion.TypeMessage.CLIMATE,climate = climate)
            }

            alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                var text = when(climate){
                    0->getString(R.string.cold)
                    1->getString(R.string.fresh)
                    2->getString(R.string.mild)
                    3->getString(R.string.torrid)
                    else -> {getString(R.string.cold)}
                }

                binding.etClimate.editText!!.setText(text)

            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        binding.btnContinue.setOnClickListener {

            val imm: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.initUserInfoParentLayout.windowToken, 0)

            weight = binding.etWeight.editText!!.text.toString()


            when {
                TextUtils.isEmpty(weight) -> showError(getString(R.string.please_input_your_weight),it)

                weight.toInt() > AppUtils.getMaxWeight(weightUnit) || weight.toInt() < AppUtils.getMinWeight(weightUnit) ->
                    showError(getString(R.string.please_input_a_valid_weight), it)

                !AppUtils.isValidDate(binding.etSleepTime.editText!!.text.toString(),binding.etWakeUpTime.editText!!.text.toString()) -> showError(getString(R.string.please_input_a_valid_rest_time), it)

                TextUtils.isEmpty(binding.etGender.editText!!.text.toString()) -> showError(getString(R.string.gender_hint),it)
                TextUtils.isEmpty(binding.etWorkType.editText!!.text.toString()) -> showError(getString(R.string.work_type_hint),it)
                TextUtils.isEmpty(binding.etClimate.editText!!.text.toString()) -> showError(getString(R.string.climate_set_hint),it)

                else -> {

                    val editor = sharedPref.edit()
                    editor.putInt(AppUtils.WEIGHT_KEY, weight.toInt())
                    editor.putInt(AppUtils.WORK_TIME_KEY, workType)
                    editor.putLong(AppUtils.WAKEUP_TIME_KEY, wakeupTime)
                    editor.putLong(AppUtils.SLEEPING_TIME_KEY, sleepingTime)
                    editor.putBoolean(AppUtils.FIRST_RUN_KEY, false)
                    editor.putBoolean(AppUtils.SET_WEIGHT_UNIT,true)
                    editor.putBoolean(AppUtils.SET_GENDER_KEY, true)
                    editor.putBoolean(AppUtils.SET_NEW_WORK_TYPE_KEY, true)
                    editor.putBoolean(AppUtils.SET_CLIMATE_KEY, true)
                    editor.putInt(AppUtils.BLOOD_DONOR_KEY, bloodDonor)
                    editor.apply()

                    val totalIntake = AppUtils.calculateIntake(weight.toInt(), workType,weightUnit, gender, climate)
                    val df = DecimalFormat("#")
                    df.roundingMode = RoundingMode.CEILING
                    editor.putFloat(AppUtils.TOTAL_INTAKE_KEY, df.format(totalIntake).toFloat())

                    editor.apply()
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        initBottomBars()
    }

    private fun initBottomBars() {
        val menu = binding.unitSystemBottomBar.menu
        val menu2 = binding.weightSystemBottomBar.menu


        for (i in AppUtils.listIdsInfoSystem.indices) {
            menu.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIdsInfoSystem[i],
                    AppUtils.listInfoSystem[i],
                    AppUtils.listStringInfoSystem[i],
                    Color.parseColor("#41B279")
                )
                    .build()
            )
        }

        for (i in AppUtils.listIdsWeightSystem.indices) {
            menu2.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIdsWeightSystem[i],
                    AppUtils.listWeightSystem[i],
                    AppUtils.listStringWeightSystem[i],
                    Color.parseColor("#41B279")
                )
                    .build()
            )
        }

        setWeightUnit()

        binding.unitSystemBottomBar.onItemSelectedListener = { _, i, _ ->
            when(i.id) {
                R.id.icon_ml -> unit = 0
                R.id.icon_oz_uk -> unit = 1
                R.id.icon_oz_us -> unit = 2

            }

            setSystemUnit()

        }

        unit = sharedPref.getInt(AppUtils.UNIT_KEY,0)

        when (unit) {
            0 -> menu.select(R.id.icon_ml)
            1 -> menu.select(R.id.icon_oz_uk)
            2 -> menu.select(R.id.icon_oz_us)
            else -> {
                menu.select(R.id.icon_ml)
                unit = 0
            }
        }

        binding.weightSystemBottomBar.onItemSelectedListener = { _, i, _ ->
            when(i.id) {
                R.id.icon_kg -> weightUnit = 0
                R.id.icon_lbl -> weightUnit = 1
            }

            setWeightUnit()

        }

    }

    private fun setSystemUnit() {
        val editor = sharedPref.edit()
        editor.putFloat(AppUtils.VALUE_50_KEY,AppUtils.firstConversion(50f,unit))
        editor.putFloat(AppUtils.VALUE_100_KEY,AppUtils.firstConversion(100f,unit))
        editor.putFloat(AppUtils.VALUE_150_KEY,AppUtils.firstConversion(150f,unit))
        editor.putFloat(AppUtils.VALUE_200_KEY,AppUtils.firstConversion(200f,unit))
        editor.putFloat(AppUtils.VALUE_250_KEY,AppUtils.firstConversion(250f,unit))
        editor.putInt(AppUtils.UNIT_NEW_KEY, unit)
        editor.apply()
    }

    private fun setWeightUnit() {
        val editor = sharedPref.edit()
        editor.putInt(AppUtils.WEIGHT_UNIT_KEY,weightUnit)
        editor.apply()
    }

    @SuppressLint("InflateParams", "RestrictedApi")
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