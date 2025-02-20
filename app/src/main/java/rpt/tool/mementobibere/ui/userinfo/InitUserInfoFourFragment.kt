package rpt.tool.mementobibere.ui.userinfo


import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentInitUserInfoFourBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.AlertHelper
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.view.inputfilter.InputFilterWeightRange


class InitUserInfoFourFragment : BaseFragment<FragmentInitUserInfoFourBinding>(FragmentInitUserInfoFourBinding::inflate) {

    var isExecute: Boolean = true
    var isExecuteSeekbar: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        body()
    }

    private fun getData(str: String): String {
        return str.replace(",", ".")
    }

    @SuppressLint("SetTextI18n")
    @Deprecated("Deprecated in Java")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (SharedPreferencesManager.setManuallyGoal) {

                AppUtils.DAILY_WATER_VALUE = SharedPreferencesManager.setManuallyGoalValue
                SharedPreferencesManager.totalIntake = AppUtils.DAILY_WATER_VALUE

                binding.lblGoal.text = getData(AppUtils.DAILY_WATER_VALUE.toString())

                if (SharedPreferencesManager.personWeightUnit) {
                    binding.lblUnit.text = "ml"
                } else {
                    binding.lblUnit.text = "fl oz"
                }
            } else {
                calculate_goal()
            }
        }
    }
    

    @SuppressLint("SetTextI18n")
    private fun body() {
        if (SharedPreferencesManager.setManuallyGoal) {
//			calculate_goal();
            AppUtils.DAILY_WATER_VALUE = SharedPreferencesManager.setManuallyGoalValue
            SharedPreferencesManager.totalIntake = AppUtils.DAILY_WATER_VALUE

            binding.lblGoal.text = getData(AppUtils.DAILY_WATER_VALUE.toString())

            if (SharedPreferencesManager.personWeightUnit) {
                binding.lblUnit.text = "ml"
            } else {
                binding.lblUnit.text = "fl oz"
            }
        } else {
            calculate_goal()
        }

        binding.lblSetGoalManually.setOnClickListener { showSetManuallyGoalDialog() }
    }

    @SuppressLint("SetTextI18n")
    fun calculate_goal() {
        val tmp_weight = "" + SharedPreferencesManager.personWeight
        val tmp_height = "" + SharedPreferencesManager.personHeight

        val isFemale: Boolean = SharedPreferencesManager.gender==1
        val isActive: Boolean = SharedPreferencesManager.workType==1
        val isPregnant: Boolean = SharedPreferencesManager.isPregnant
        val isBreastfeeding: Boolean = SharedPreferencesManager.isBreastfeeding
        val weatherIdx: Int = SharedPreferencesManager.climate

        if (!AppUtils.checkBlankData(tmp_weight)) {
            var tot_drink = 0.0f
            var tmp_kg = 0.0
            tmp_kg = if (SharedPreferencesManager.personWeightUnit) {
                ("" + tmp_weight).toDouble()
            } else {
                AppUtils.lbToKgConverter(tmp_weight.toDouble())
            }

            var tmp_cm = 0.0
            tmp_cm = if (SharedPreferencesManager.personHeightUnit) {
                ("" + tmp_height).toDouble()
            } else {
                AppUtils.feetToCmConverter(tmp_height.toDouble())
            }


            tot_drink =
                if (isFemale) ((if (isActive) tmp_kg * AppUtils.ACTIVE_FEMALE_WATER else tmp_kg *
                        AppUtils.FEMALE_WATER).toFloat()+(tmp_cm/3f)).toFloat()
                else (if (isActive) tmp_kg * AppUtils.ACTIVE_MALE_WATER else tmp_kg *
                        AppUtils.MALE_WATER+(tmp_cm/3f)).toFloat()

            tot_drink *= when (weatherIdx) {
                1 -> AppUtils.WEATHER_CLOUDY
                2 -> AppUtils.WEATHER_RAINY
                3 -> AppUtils.WEATHER_SNOW
                else -> AppUtils.WEATHER_SUNNY
            }

            if (isPregnant && isFemale) {
                tot_drink += AppUtils.PREGNANT_WATER
            }

            if (isBreastfeeding && isFemale) {
                tot_drink += AppUtils.BREASTFEEDING_WATER
            }

            if (tot_drink < 900) tot_drink = 900.0f

            if (tot_drink > 8000) tot_drink = 8000.0f

            val tot_drink_fl_oz: Float = AppUtils.mlToOzUS(tot_drink)

            if (SharedPreferencesManager.personWeightUnit) {
  
                binding.lblUnit.text = "ml"
                AppUtils.DAILY_WATER_VALUE = tot_drink
            } else {
                binding.lblUnit.text = "fl oz"
                AppUtils.DAILY_WATER_VALUE = tot_drink_fl_oz
            }

            AppUtils.DAILY_WATER_VALUE = Math.round(AppUtils.DAILY_WATER_VALUE).toFloat()
            binding.lblGoal.text = getData("" + AppUtils.DAILY_WATER_VALUE.toInt())
            
            SharedPreferencesManager.totalIntake = AppUtils.DAILY_WATER_VALUE
        }
    }


    @SuppressLint("InflateParams")
    private fun showSetManuallyGoalDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val view: View =
            LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_set_manually_goal,
                null, false)


        val lbl_goal2 = view.findViewById<AppCompatEditText>(R.id.lbl_goal)
        val lbl_unit2 = view.findViewById<AppCompatTextView>(R.id.lbl_unit)
        val btn_cancel = view.findViewById<RelativeLayout>(R.id.btn_cancel)
        val btn_save = view.findViewById<RelativeLayout>(R.id.btn_save)
        val seekbarGoal = view.findViewById<SeekBar>(R.id.seekbarGoal)

        
        if (SharedPreferencesManager.setManuallyGoal) lbl_goal2.setText(
            getData(
                SharedPreferencesManager.setManuallyGoalValue.toString()
            )
        )
        else lbl_goal2.setText(getData("" + (SharedPreferencesManager.totalIntake)))

        lbl_unit2.text = if (SharedPreferencesManager.personWeightUnit) "ml" else "fl oz"



        if (SharedPreferencesManager.personWeightUnit) {
            seekbarGoal.min = 900
            seekbarGoal.max = 8000
            lbl_goal2.filters =
                arrayOf<InputFilter>(InputFilterWeightRange(0.0, 8000.0), LengthFilter(4))
            //lbl_goal2.setMaxL
        } else {
            seekbarGoal.min = 30
            seekbarGoal.max = 270
            lbl_goal2.filters =
                arrayOf<InputFilter>(InputFilterWeightRange(0.0, 270.0), LengthFilter(3))
        }

        val f =
            if (SharedPreferencesManager.setManuallyGoal) 
                SharedPreferencesManager.setManuallyGoalValue 
            else SharedPreferencesManager.totalIntake
        seekbarGoal.progress = f.toInt()

        lbl_goal2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                isExecuteSeekbar = false
            }

            override fun afterTextChanged(editable: Editable) {
                try {
                    if (!AppUtils.checkBlankData(
                            lbl_goal2.text.toString().trim { it <= ' ' }) && isExecute
                    ) {
                        val data = lbl_goal2.text.toString().trim { it <= ' ' }.toInt()
                        seekbarGoal.progress = data
                    }
                } catch (e: Exception) {
                    e.message?.let { e(Throwable(e), it) }
                }

                isExecuteSeekbar = true
            }
        })

        seekbarGoal.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBars: SeekBar, progress: Int, fromUser: Boolean) {
                var progress = progress
                if (isExecuteSeekbar) {

                    lbl_goal2.setText("" + progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isExecute = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isExecute = true
            }
        })

        btn_cancel.setOnClickListener { dialog.dismiss() }

        btn_save.setOnClickListener {
            if (SharedPreferencesManager.personWeightUnit && lbl_goal2.text.toString()
                    .trim { it <= ' ' }.toFloat() >= 900
            ) {
                AppUtils.DAILY_WATER_VALUE =
                    lbl_goal2.text.toString().trim { it <= ' ' }.toFloat()
                SharedPreferencesManager.totalIntake = AppUtils.DAILY_WATER_VALUE
                binding.lblGoal.text = getData("" + AppUtils.DAILY_WATER_VALUE)
                SharedPreferencesManager.setManuallyGoal = true
                SharedPreferencesManager.setManuallyGoalValue = AppUtils.DAILY_WATER_VALUE
                dialog.dismiss()
            } else {
                if (!SharedPreferencesManager.personWeightUnit && lbl_goal2.text.toString()
                        .trim { it <= ' ' }.toFloat() >= 30
                ) {
                    AppUtils.DAILY_WATER_VALUE =
                        lbl_goal2.text.toString().trim { it <= ' ' }.toFloat()
                    SharedPreferencesManager.totalIntake = AppUtils.DAILY_WATER_VALUE
                    binding.lblGoal.text = getData("" + AppUtils.DAILY_WATER_VALUE)
                    SharedPreferencesManager.setManuallyGoal = true
                    SharedPreferencesManager.setManuallyGoalValue = AppUtils.DAILY_WATER_VALUE
                    dialog.dismiss()
                } else {
                    val alertHelper = AlertHelper(requireContext())
                    alertHelper.customAlert(requireContext()
                        .getString(R.string.str_set_daily_goal_validation))
                }
            }
        }

        dialog.setContentView(view)

        dialog.show()
    }
}