package rpt.tool.mementobibere.ui.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentProfileBinding
import rpt.tool.mementobibere.ui.widget.NewAppWidget
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.AlertHelper
import rpt.tool.mementobibere.utils.helpers.StringHelper
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.view.inputfilter.DigitsInputFilter
import rpt.tool.mementobibere.utils.view.inputfilter.InputFilterRange
import rpt.tool.mementobibere.utils.view.inputfilter.InputFilterWeightRange
import java.util.Locale


class ProfileFragment:
    BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    var mDropdown: PopupWindow? = null
    var isExecute: Boolean = true
    var isExecuteSeekbar: Boolean = true
    var weight_kg_lst: MutableList<String> = ArrayList()
    var weight_lb_lst: MutableList<String> = ArrayList()
    var height_cm_lst: MutableList<String> = ArrayList()
    var height_feet_lst: MutableList<String> = ArrayList()
    var height_feet_elements: MutableList<Double> = ArrayList()
    var mDropdownWeather: PopupWindow? = null
    var stringHelper: StringHelper? = null
    var alertHelper: AlertHelper? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.navigationBarColor = requireContext().resources.getColor(R.color.water_color)

        AppUtils.DAILY_WATER_VALUE=SharedPreferencesManager.totalIntake

        alertHelper = AlertHelper(requireContext())

        convertUpperCase(binding.lblGender)
        convertUpperCase(binding.lblWeight)
        convertUpperCase(binding.lblHeight)
        convertUpperCase(binding.lblGoal)
        convertUpperCase(binding.lblActive)
        convertUpperCase(binding.lblBlood)
        convertUpperCase(binding.lblPregnant)
        convertUpperCase(binding.lblBreastfeeding)
        convertUpperCase(binding.lblWeather)
        convertUpperCase(binding.lblOtherFactor)

        stringHelper = StringHelper(requireContext(),requireActivity())

        binding.txtUserName.text = SharedPreferencesManager.userName
        binding.txtGender.text = if (SharedPreferencesManager.gender == 1)
            requireContext().getString(R.string.str_female) else requireContext().getString(
            R.string.str_male
        )

        loadPhoto()

        val str: String = SharedPreferencesManager.personHeight + " " +
                (if (SharedPreferencesManager.personHeightUnit) "cm" else "feet")

        binding.txtHeight.text = str

        val str2: String =
            (if (SharedPreferencesManager.personWeightUnit) AppUtils.decimalFormat2.format(
                SharedPreferencesManager.personWeight.toDouble()
            ) + " kg" else SharedPreferencesManager.personWeight + " lb")
        binding.txtWeight.text = str2

        val str3 = (getData("" + AppUtils.DAILY_WATER_VALUE) + " "
                + (if (SharedPreferencesManager.personWeightUnit) "ml" else "fl oz"))
        binding.txtGoal.text = str3

        header()
        body()

        init_WeightKG()
        init_WeightLB()
        init_HeightCM()
        init_HeightFeet()

        loadHeightData()

    }

    private fun loadHeightData() {
        height_feet_elements.clear()

        height_feet_elements.add(2.0)
        height_feet_elements.add(2.1)
        height_feet_elements.add(2.2)
        height_feet_elements.add(2.3)
        height_feet_elements.add(2.4)
        height_feet_elements.add(2.5)
        height_feet_elements.add(2.6)
        height_feet_elements.add(2.7)
        height_feet_elements.add(2.8)
        height_feet_elements.add(2.9)
        height_feet_elements.add(2.10)
        height_feet_elements.add(2.11)
        height_feet_elements.add(3.0)
        height_feet_elements.add(3.1)
        height_feet_elements.add(3.2)
        height_feet_elements.add(3.3)
        height_feet_elements.add(3.4)
        height_feet_elements.add(3.5)
        height_feet_elements.add(3.6)
        height_feet_elements.add(3.7)
        height_feet_elements.add(3.8)
        height_feet_elements.add(3.9)
        height_feet_elements.add(3.10)
        height_feet_elements.add(3.11)
        height_feet_elements.add(4.0)
        height_feet_elements.add(4.1)
        height_feet_elements.add(4.2)
        height_feet_elements.add(4.3)
        height_feet_elements.add(4.4)
        height_feet_elements.add(4.5)
        height_feet_elements.add(4.6)
        height_feet_elements.add(4.7)
        height_feet_elements.add(4.8)
        height_feet_elements.add(4.9)
        height_feet_elements.add(4.10)
        height_feet_elements.add(4.11)
        height_feet_elements.add(5.0)
        height_feet_elements.add(5.1)
        height_feet_elements.add(5.2)
        height_feet_elements.add(5.3)
        height_feet_elements.add(5.4)
        height_feet_elements.add(5.5)
        height_feet_elements.add(5.6)
        height_feet_elements.add(5.7)
        height_feet_elements.add(5.8)
        height_feet_elements.add(5.9)
        height_feet_elements.add(5.10)
        height_feet_elements.add(5.11)
        height_feet_elements.add(6.0)
        height_feet_elements.add(6.1)
        height_feet_elements.add(6.2)
        height_feet_elements.add(6.3)
        height_feet_elements.add(6.4)
        height_feet_elements.add(6.5)
        height_feet_elements.add(6.6)
        height_feet_elements.add(6.7)
        height_feet_elements.add(6.8)
        height_feet_elements.add(6.9)
        height_feet_elements.add(6.10)
        height_feet_elements.add(6.11)
        height_feet_elements.add(7.0)
        height_feet_elements.add(7.1)
        height_feet_elements.add(7.2)
        height_feet_elements.add(7.3)
        height_feet_elements.add(7.4)
        height_feet_elements.add(7.5)
        height_feet_elements.add(7.6)
        height_feet_elements.add(7.7)
        height_feet_elements.add(7.8)
        height_feet_elements.add(7.9)
        height_feet_elements.add(7.10)
        height_feet_elements.add(7.11)
        height_feet_elements.add(8.0)
    }

    private fun loadPhoto() {
        Glide.with(requireActivity()).load(
            if (SharedPreferencesManager.gender == 1)
                R.drawable.female_white
            else
                R.drawable.male_white
        ).apply(RequestOptions.circleCropTransform())
            .into(binding.imgUser)
    }

    private fun getData(str: String): String {
        return str.replace(",", ".")
    }

    private fun convertUpperCase(appCompatTextView: AppCompatTextView) {
        appCompatTextView.text = appCompatTextView.text.toString().uppercase(Locale.getDefault())
    }

    private fun header() {
        binding.headerBlock.lblToolbarTitle.text = requireContext().getString(R.string.str_my_profile)
        
        binding.headerBlock.leftIconBlock.setOnClickListener { finish() }

        binding.headerBlock.rightIconBlock.visibility = View.GONE
    }

    private fun finish() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
    }

    fun body() {
        
        binding.genderBlock.setOnClickListener { v -> //showGenderMenu(v);
            initiatePopupWindow(v)
        }
        
        binding.editUserNameBlock.setOnClickListener { openNameDialog() }

        binding.goalBlock.setOnClickListener { showSetManuallyGoalDialog() }

        binding.heightBlock.setOnClickListener { openHeightDialog() }

        binding.weightBlock.setOnClickListener { openWeightDialog() }

        binding.switchActive.setChecked(SharedPreferencesManager.workType == 1)
        
        binding.switchActive.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferencesManager.workType = if(isChecked) 1 else 0
            val tmp_weight = "" + SharedPreferencesManager.personWeight
            val isFemale: Boolean = SharedPreferencesManager.gender == 1
            val min = (if (SharedPreferencesManager.personWeightUnit) 900 else 30).toFloat()
            val max = (if (SharedPreferencesManager.personWeightUnit) 8000 else 270).toFloat()
            val weatherIdx: Int = SharedPreferencesManager.climate

            d("maxmaxmaxmax : ", "$max @@@ $min  @@@  $tmp_weight")

            var tmp_kg = 0.0f
            tmp_kg = if (SharedPreferencesManager.personWeightUnit) {
                ("" + tmp_weight).toFloat()
            } else {
                AppUtils.lbToKgConverter(tmp_weight.toDouble()).toFloat()
            }

            d("maxmaxmaxmax : ", "" + tmp_kg)

            var diff = 0.0f

            diff = if (isFemale) (tmp_kg * AppUtils.DEACTIVE_FEMALE_WATER).toFloat()
            else (tmp_kg * AppUtils.DEACTIVE_MALE_WATER).toFloat()

            d("maxmaxmaxmax DIFF : ", "" + diff)

            val x= when (weatherIdx) {
                1 -> AppUtils.WEATHER_CLOUDY
                2 -> AppUtils.WEATHER_RAINY
                3 -> AppUtils.WEATHER_SNOW
                else -> AppUtils.WEATHER_SUNNY
            }
            
            diff *= x


            d("maxmaxmaxmax : ", "" + diff + " @@@ " + AppUtils.DAILY_WATER_VALUE)

            if (isChecked) {
                if (SharedPreferencesManager.personWeightUnit) {
                    AppUtils.DAILY_WATER_VALUE += diff
                } else {
                    AppUtils.DAILY_WATER_VALUE += AppUtils.mlToOzUS(diff)
                }

                if (AppUtils.DAILY_WATER_VALUE > max) AppUtils.DAILY_WATER_VALUE = max
            } else {
                if (SharedPreferencesManager.personWeightUnit) {
                    AppUtils.DAILY_WATER_VALUE -= diff
                } else {
                    AppUtils.DAILY_WATER_VALUE -= AppUtils.mlToOzUS(diff)
                }

                if (AppUtils.DAILY_WATER_VALUE > max) AppUtils.DAILY_WATER_VALUE = max
            }

            AppUtils.DAILY_WATER_VALUE = Math.round(AppUtils.DAILY_WATER_VALUE).toFloat()

            val str = getData("" + AppUtils.DAILY_WATER_VALUE) + " " +
                    (if (SharedPreferencesManager.personWeightUnit) "ml" else "fl oz")

            binding.txtGoal.text = str
            SharedPreferencesManager.totalIntake = AppUtils.DAILY_WATER_VALUE
        }
        
        binding.switchBreastfeeding.setChecked(SharedPreferencesManager.isBreastfeeding)
        
        binding.switchBreastfeeding.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferencesManager.isBreastfeeding = isChecked
            setSwitchData(isChecked, AppUtils.BREASTFEEDING_WATER)
        }
        
        binding.switchPregnant.setChecked(SharedPreferencesManager.isPregnant)
        
        binding.switchPregnant.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferencesManager.isPregnant = isChecked
            setSwitchData(isChecked, AppUtils.PREGNANT_WATER)
        }
        
        binding.switchBloodDonor.isChecked = SharedPreferencesManager.bloodDonorKey == 1
        
        binding.switchBloodDonor.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferencesManager.bloodDonorKey = if(isChecked) 1 else 0
        }

        binding.otherFactors.visibility = if (SharedPreferencesManager.gender == 1) View.VISIBLE else View.GONE

        var str = ""
        str = when (SharedPreferencesManager.climate) {
            1 -> requireContext().getString(R.string.cloudy)
            2 -> requireContext().getString(R.string.rainy)
            3 -> requireContext().getString(R.string.snow)
            else -> requireContext().getString(R.string.sunny)
        }
        binding.txtWeather.text = str

        binding.weatherBlock.setOnClickListener {
            initiateWeatherPopupWindow(
                binding.switchActive
            )
        }

        calculateActiveValue()
    }

    private fun setSwitchData(isChecked: Boolean, water: Float) {
        var diff = 0.0f
        val min = (if (SharedPreferencesManager.personWeightUnit) 900 else 30).toFloat()
        val max = (if (SharedPreferencesManager.personWeightUnit) 8000 else 270).toFloat()

        diff = if (SharedPreferencesManager.personWeightUnit) water
        else AppUtils.mlToOzUS(water)


        if (isChecked) {
            AppUtils.DAILY_WATER_VALUE += diff

            if (AppUtils.DAILY_WATER_VALUE > max) AppUtils.DAILY_WATER_VALUE = max
        } else {
            AppUtils.DAILY_WATER_VALUE -= diff

            if (AppUtils.DAILY_WATER_VALUE < min) AppUtils.DAILY_WATER_VALUE = min
        }


        AppUtils.DAILY_WATER_VALUE = Math.round(AppUtils.DAILY_WATER_VALUE).toFloat()

        val str = getData("" + AppUtils.DAILY_WATER_VALUE) + " " +
                (if (SharedPreferencesManager.personWeightUnit) "ml" else "fl oz")

        binding.txtGoal.text = str

        SharedPreferencesManager.totalIntake = AppUtils.DAILY_WATER_VALUE

        calculateActiveValue()
    }
    

    @SuppressLint("InflateParams")
    private fun initiatePopupWindow(v: View): PopupWindow {
        try {
            val mInflater = requireContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout: View = mInflater.inflate(R.layout.row_item_gender, null)

            //If you want to add any listeners to your textviews, these are two //textviews.
            val lbl_male = layout.findViewById<TextView>(R.id.lbl_male)

            lbl_male.text = requireContext().getString(R.string.str_male)


            val lbl_female = layout.findViewById<TextView>(R.id.lbl_female)

            lbl_female.text = requireContext().getString(R.string.str_female)

            lbl_male.setOnClickListener {
                SharedPreferencesManager.gender = 0
                loadPhoto()
                mDropdown!!.dismiss()
                binding.txtGender.text = requireContext().getString(R.string.str_male)
                binding.otherFactors.visibility = View.GONE
                binding.switchBreastfeeding.setChecked(false)
                binding.switchPregnant.setChecked(false)
                calculate_goal()
            }

            lbl_female.setOnClickListener {
                SharedPreferencesManager.gender = 1
                loadPhoto()
                mDropdown!!.dismiss()
                binding.txtGender.text = requireContext().getString(R.string.str_female)
                binding.otherFactors.visibility = View.VISIBLE
                calculate_goal()
            }

            layout.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )
            mDropdown = PopupWindow(
                layout, FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, true
            )
            mDropdown!!.showAsDropDown(v, 5, 5)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mDropdown!!
    }

    @SuppressLint("InflateParams")
    private fun initiateWeatherPopupWindow(v: View): PopupWindow {
        try {
            val mInflater = requireContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout: View = mInflater.inflate(R.layout.row_item_weather, null)

            //If you want to add any listeners to your textviews, these are two //textviews.
            val lbl_sunny = layout.findViewById<TextView>(R.id.lbl_sunny)
            lbl_sunny.text = requireContext().getString(R.string.sunny)

            val lbl_cloudy = layout.findViewById<TextView>(R.id.lbl_cloudy)
            lbl_cloudy.text = requireContext().getString(R.string.cloudy)

            val lbl_rainy = layout.findViewById<TextView>(R.id.lbl_rainy)
            lbl_rainy.text = requireContext().getString(R.string.rainy)

            val lbl_snow = layout.findViewById<TextView>(R.id.lbl_snow)
            lbl_snow.text = requireContext().getString(R.string.snow)

            lbl_sunny.setOnClickListener {
                SharedPreferencesManager.climate = 0
                mDropdownWeather!!.dismiss()
                binding.txtWeather.text = requireContext().getString(R.string.sunny)
                calculate_goal()
            }

            lbl_cloudy.setOnClickListener {
                SharedPreferencesManager.climate = 1
                mDropdownWeather!!.dismiss()
                binding.txtWeather.text = requireContext().getString(R.string.cloudy)
                calculate_goal()
            }

            lbl_rainy.setOnClickListener {
                SharedPreferencesManager.climate = 2
                mDropdownWeather!!.dismiss()
                binding.txtWeather.text = requireContext().getString(R.string.rainy)
                calculate_goal()
            }

            lbl_snow.setOnClickListener {
                SharedPreferencesManager.climate = 3
                mDropdownWeather!!.dismiss()
                binding.txtWeather.text = requireContext().getString(R.string.snow)
                calculate_goal()
            }

            layout.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )
            mDropdownWeather = PopupWindow(
                layout, FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, true
            )

            mDropdownWeather!!.showAsDropDown(v, 5, 5)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mDropdownWeather!!
    }

    @SuppressLint("InflateParams")
    private fun openNameDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val view: View = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_user_name,
            null, false)

        val btn_cancel = view.findViewById<RelativeLayout>(R.id.btn_cancel)
        val btn_add = view.findViewById<RelativeLayout>(R.id.btn_add)
        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)

        val txt_name = view.findViewById<AppCompatEditText>(R.id.txt_name)

        txt_name.requestFocus()

        btn_cancel.setOnClickListener { dialog.cancel() }

        img_cancel.setOnClickListener { dialog.cancel() }

        txt_name.setText(SharedPreferencesManager.userName)
        txt_name.setSelection(txt_name.text.toString().trim { it <= ' ' }.length)

        btn_add.setOnClickListener {
            if (AppUtils.checkBlankData(txt_name.text.toString().trim { it <= ' ' })) {
                alertHelper!!.customAlert(requireContext().getString(R.string.str_your_name_validation))
            } else if (txt_name.text.toString().trim { it <= ' ' }.length < 3) {
                alertHelper!!.customAlert(requireContext().getString(R.string.str_valid_name_validation))
            } else {
                SharedPreferencesManager.userName =
                    txt_name.text.toString().trim { it <= ' ' }

                binding.txtUserName.text = SharedPreferencesManager.userName

                dialog.dismiss()
            }
        }

        dialog.setContentView(view)

        dialog.show()
    }

    @SuppressLint("InflateParams", "SetTextI18n")
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
                "" + SharedPreferencesManager.setManuallyGoalValue)
        )
        else lbl_goal2.setText(getData("" + (SharedPreferencesManager.totalIntake)))

        lbl_unit2.text = if (SharedPreferencesManager.personWeightUnit) "ml" else "fl oz"

        if (SharedPreferencesManager.personWeightUnit) {
            seekbarGoal.min = 900
            seekbarGoal.max = 8000
            lbl_goal2.filters =
                arrayOf(InputFilterWeightRange(0.0, 8000.0), LengthFilter(4))
        } else {
            seekbarGoal.min = 30
            seekbarGoal.max = 270
            lbl_goal2.filters =
                arrayOf(InputFilterWeightRange(0.0, 270.0), LengthFilter(3))
        }

        val f =
            if (SharedPreferencesManager.setManuallyGoal)
                SharedPreferencesManager.setManuallyGoalValue else
                    SharedPreferencesManager.totalIntake
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
            val unit = if (SharedPreferencesManager.personWeightUnit) "ml" else "fl oz"
            if (SharedPreferencesManager.personWeightUnit && lbl_goal2.text.toString()
                    .trim { it <= ' ' }.toFloat() >= 900
            ) {
                AppUtils.DAILY_WATER_VALUE =
                    lbl_goal2.text.toString().trim { it <= ' ' }.toFloat()
                SharedPreferencesManager.totalIntake = AppUtils.DAILY_WATER_VALUE
                binding.txtGoal.text = getData("" + AppUtils.DAILY_WATER_VALUE) + " " + unit
                SharedPreferencesManager.setManuallyGoal = true
                SharedPreferencesManager.setManuallyGoalValue = AppUtils.DAILY_WATER_VALUE
                dialog.dismiss()

                refreshWidget()
            } else {
                if (!SharedPreferencesManager.personWeightUnit && lbl_goal2.text.toString()
                        .trim { it <= ' ' }.toFloat() >= 30
                ) {
                    AppUtils.DAILY_WATER_VALUE =
                        lbl_goal2.text.toString().trim { it <= ' ' }.toFloat()
                    SharedPreferencesManager.totalIntake = AppUtils.DAILY_WATER_VALUE
                    binding.txtGoal.text = getData("" + AppUtils.DAILY_WATER_VALUE) +
                            " " + unit

                    SharedPreferencesManager.setManuallyGoal = true
                    SharedPreferencesManager.setManuallyGoalValue = AppUtils.DAILY_WATER_VALUE
                    dialog.dismiss()

                    refreshWidget()
                } else {
                    alertHelper!!.customAlert(requireContext().getString(R.string.str_set_daily_goal_validation))
                }
            }
        }

        dialog.setContentView(view)

        dialog.show()
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun openHeightDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val view: View = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_height, null, false)

        val btn_cancel = view.findViewById<RelativeLayout>(R.id.btn_cancel)
        val btn_add = view.findViewById<RelativeLayout>(R.id.btn_add)
        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)
        val txt_name = view.findViewById<AppCompatEditText>(R.id.txt_name)

        val rdo_cm = view.findViewById<RadioButton>(R.id.rdo_cm)
        val rdo_feet = view.findViewById<RadioButton>(R.id.rdo_feet)

        txt_name.requestFocus()

        rdo_cm.setOnClickListener {
            if (!AppUtils.checkBlankData(txt_name.text.toString())) {
                var final_height_cm = 61

                try {
                    val tmp_height = getData(txt_name.text.toString().trim { it <= ' ' })

                    val d = (txt_name.text.toString().trim { it <= ' ' }.toFloat()) as Int

                    d("after_decimal", "" + tmp_height.indexOf("."))

                    if (tmp_height.indexOf(".") > 0) {
                        val after_decimal = tmp_height.substring(
                            tmp_height.indexOf(".") + 1)

                        if (!AppUtils.checkBlankData(after_decimal)) {
                            val after_decimal_int = after_decimal.toInt()

                            val final_height = ((d * 12) + after_decimal_int).toDouble()

                            final_height_cm = Math.round(final_height * 2.54).toInt()
                            
                        } else {
                            final_height_cm = Math.round(d * 12 * 2.54).toInt()
                            
                        }
                    } else {
                        final_height_cm = Math.round(d * 12 * 2.54).toInt()
                        
                    }
                } catch (e: Exception) {
                    e.message?.let { it1 -> e(Throwable(e), it1) }
                }

                rdo_feet.isClickable = true
                rdo_cm.isClickable = false
                txt_name.filters = arrayOf<InputFilter>(DigitsInputFilter(3, 0, 240.0))
                txt_name.setText(getData("" + final_height_cm))
                txt_name.setSelection(txt_name.length())
                
            } else {
                rdo_feet.isChecked = true
                rdo_cm.isChecked = false
            }
        }

        rdo_feet.setOnClickListener {
            if (!AppUtils.checkBlankData(txt_name.text.toString())) {
                var final_height_feet = "5.0"

                try {
                    val d = (txt_name.text.toString().trim { it <= ' ' }.toFloat()) as Int

                    val tmp_height_inch = Math.round(d / 2.54).toInt()

                    val first = tmp_height_inch / 12
                    val second = tmp_height_inch % 12
                    
                    final_height_feet = "$first.$second"
                } catch (e: Exception) {
                    e.message?.let { it1 -> e(Throwable(e), it1) }
                }

                rdo_feet.isClickable = false
                rdo_cm.isClickable = true
                txt_name.filters =
                    arrayOf<InputFilter>(InputFilterRange(0.00, height_feet_elements))
                txt_name.setText(getData(final_height_feet))
                txt_name.setSelection(txt_name.length())
                
            } else {
                rdo_feet.isChecked = false
                rdo_cm.isChecked = true
            }
        }

        if (SharedPreferencesManager.personHeightUnit) {
            rdo_cm.isChecked = true
            rdo_cm.isClickable = false
            rdo_feet.isClickable = true
        } else {
            rdo_feet.isChecked = true
            rdo_cm.isClickable = true
            rdo_feet.isClickable = false
        }

        if (!AppUtils.checkBlankData(SharedPreferencesManager.personHeight)) {
            if (rdo_cm.isChecked) {
                txt_name.filters = arrayOf<InputFilter>(DigitsInputFilter(
                    3, 0, 240.0))
                txt_name.setText(getData(SharedPreferencesManager.personHeight))
            } else {
                txt_name.filters =
                    arrayOf<InputFilter>(InputFilterRange(0.00, height_feet_elements))
                txt_name.setText(getData(SharedPreferencesManager.personHeight))
            }
        } else {
            if (rdo_cm.isChecked) {
                txt_name.filters = arrayOf<InputFilter>(DigitsInputFilter(3,
                    0, 240.0))
                txt_name.setText("150")
            } else {
                txt_name.filters =
                    arrayOf<InputFilter>(InputFilterRange(0.00, height_feet_elements))
                txt_name.setText("5.0")
            }
        }

        btn_cancel.setOnClickListener { dialog.cancel() }

        img_cancel.setOnClickListener { dialog.cancel() }

        txt_name.setSelection(txt_name.text.toString().length)

        btn_add.setOnClickListener {
            if (AppUtils.checkBlankData(txt_name.text.toString().trim { it <= ' ' })) {
                alertHelper!!.customAlert(requireContext().getString(R.string.str_height_validation))
            } else {
                var str = txt_name.text.toString().trim { it <= ' ' }

                if (rdo_feet.isChecked) {
                    if (!str.contains(".11") && !str.contains(".10")) str =
                        AppUtils.decimalFormat2.format(str.toDouble())
                }

                str += " " + (if (rdo_feet.isChecked) "feet" else "cm")

                binding.txtHeight.text = str

                SharedPreferencesManager.personWeightUnit = rdo_cm.isChecked

                saveData(txt_name)

                dialog.dismiss()
            }
        }

        dialog.setContentView(view)

        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun openWeightDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val view: View = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_weight, null, false)

        val btn_cancel = view.findViewById<RelativeLayout>(R.id.btn_cancel)
        val btn_add = view.findViewById<RelativeLayout>(R.id.btn_add)
        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)
        val txt_name = view.findViewById<AppCompatEditText>(R.id.txt_name)


        val rdo_kg = view.findViewById<RadioButton>(R.id.rdo_kg)
        val rdo_lb = view.findViewById<RadioButton>(R.id.rdo_lb)

        txt_name.requestFocus()

        rdo_kg.setOnClickListener {
            if (!AppUtils.checkBlankData(txt_name.text.toString())) {
                val weight_in_lb = txt_name.text.toString().toDouble()

                var weight_in_kg = 0.0

                if (weight_in_lb > 0) weight_in_kg =
                    Math.round(AppUtils.lbToKgConverter(weight_in_lb)).toDouble()

                val tmp = weight_in_kg.toInt()

                txt_name.filters = arrayOf<InputFilter>(InputFilterWeightRange(0.0, 130.0))
                txt_name.setText(getData("" + AppUtils.decimalFormat2.format(tmp)))
                rdo_kg.isClickable = false
                rdo_lb.isClickable = true
            }
        }

        rdo_lb.setOnClickListener {
            if (!AppUtils.checkBlankData(txt_name.text.toString())) {
                val weight_in_kg = txt_name.text.toString().toDouble()

                var weight_in_lb = 0.0

                if (weight_in_kg > 0) weight_in_lb =
                    Math.round(AppUtils.kgToLbConverter(weight_in_kg)).toDouble()

                val tmp = weight_in_lb.toInt()

                txt_name.filters = arrayOf<InputFilter>(
                    DigitsInputFilter(3,
                    0, 287.0)
                )
                txt_name.setText(getData("" + tmp))
                rdo_kg.isClickable = true
                rdo_lb.isClickable = false
            }
        }

        if (SharedPreferencesManager.personWeightUnit) {
            rdo_kg.isChecked = true
            rdo_kg.isClickable = false
            rdo_lb.isClickable = true
        } else {
            rdo_lb.isChecked = true
            rdo_kg.isClickable = true
            rdo_lb.isClickable = false
        }

        if (!AppUtils.checkBlankData(SharedPreferencesManager.personWeight)) {
            if (rdo_kg.isChecked) {

                txt_name.filters = arrayOf<InputFilter>(InputFilterWeightRange(0.0, 130.0))
                txt_name.setText(getData(SharedPreferencesManager.personWeight))
            } else {
                txt_name.filters = arrayOf<InputFilter>(DigitsInputFilter(3, 
                    0, 287.0))
                txt_name.setText(getData(SharedPreferencesManager.personWeight))
            }
        } else {
            if (rdo_kg.isChecked) {
                txt_name.filters = arrayOf<InputFilter>(InputFilterWeightRange(0.0, 130.0))
                txt_name.setText("80.0")
            } else {
                
                txt_name.filters = arrayOf<InputFilter>(DigitsInputFilter(3,
                    0, 287.0))
                txt_name.setText("176")
            }
        }

        btn_cancel.setOnClickListener { dialog.cancel() }

        img_cancel.setOnClickListener { dialog.cancel() }

        txt_name.setSelection(txt_name.text.toString().length)

        btn_add.setOnClickListener {
            if (AppUtils.checkBlankData(txt_name.text.toString().trim { it <= ' ' })) {
                alertHelper!!.customAlert(requireContext().getString(R.string.str_weight_validation))
            } else {
                var str = txt_name.text.toString().trim { it <= ' ' }

                if (rdo_kg.isChecked) {
                    str = AppUtils.decimalFormat2.format(str.toDouble())
                }
                
                str += " " + (if (rdo_kg.isChecked) "kg" else "lb")

                binding.txtWeight.text = str

                saveWeightData(txt_name)

                SharedPreferencesManager.personWeightUnit = rdo_kg.isChecked

                SharedPreferencesManager.unitString = if (rdo_kg.isChecked) "ml" else "fl oz"

                calculate_goal()

                dialog.dismiss()
            }
        }

        dialog.setContentView(view)

        dialog.show()
    }
    
    private fun calculate_goal() {
        val tmp_weight = "" + SharedPreferencesManager.personWeight

        val isFemale: Boolean = SharedPreferencesManager.gender == 1
        val isActive: Boolean = SharedPreferencesManager.workType == 1
        val isPregnant: Boolean = SharedPreferencesManager.isPregnant
        val isBreastfeeding: Boolean = SharedPreferencesManager.isBreastfeeding
        val weatherIdx: Int = SharedPreferencesManager.climate

        if (!AppUtils.checkBlankData(tmp_weight)) {
            var tot_drink = 0.0f
            var tmp_kg = 0.0f
            tmp_kg = if (SharedPreferencesManager.personWeightUnit) {
                ("" + tmp_weight).toFloat()
            } else {
                AppUtils.lbToKgConverter(tmp_weight.toDouble()).toFloat()
            }

            tot_drink =
                if (isFemale) if (isActive) tmp_kg * AppUtils.ACTIVE_FEMALE_WATER else tmp_kg *
                        AppUtils.FEMALE_WATER
                else if (isActive) tmp_kg * AppUtils.ACTIVE_MALE_WATER else tmp_kg *
                        AppUtils.MALE_WATER

            tot_drink *= if (weatherIdx == 1) AppUtils.WEATHER_CLOUDY
            else if (weatherIdx == 2) AppUtils.WEATHER_RAINY
            else if (weatherIdx == 3) AppUtils.WEATHER_SNOW
            else AppUtils.WEATHER_SUNNY

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
                AppUtils.DAILY_WATER_VALUE = tot_drink
            } else {
                AppUtils.DAILY_WATER_VALUE = tot_drink_fl_oz
            }

            AppUtils.DAILY_WATER_VALUE = Math.round(AppUtils.DAILY_WATER_VALUE).toFloat()

            val str = getData("" + AppUtils.DAILY_WATER_VALUE) + " " +
                    (if (SharedPreferencesManager.personWeightUnit) "ml" else "fl oz")

            binding.txtGoal.text = str

            SharedPreferencesManager.totalIntake = AppUtils.DAILY_WATER_VALUE

            refreshWidget()

            calculateActiveValue()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateActiveValue() {
        var pstr = ""

        pstr = if (SharedPreferencesManager.personWeightUnit) {
            (AppUtils.PREGNANT_WATER).toString() + " ml"
        } else {
            (Math.round(AppUtils.mlToOzUS(AppUtils.PREGNANT_WATER))).toString() + " fl oz"
        }

        binding.lblPregnant.text = requireContext().getString(R.string.pregnant)
        convertUpperCase(binding.lblPregnant)
        binding.lblPregnant.text = binding.lblPregnant.getText().toString() + " (+" + pstr + ")"

        //====================================
        var bstr = ""

        bstr = if (SharedPreferencesManager.personWeightUnit) {
            (AppUtils.BREASTFEEDING_WATER).toString() + " ml"
        } else {
            (Math.round(AppUtils.mlToOzUS(AppUtils.BREASTFEEDING_WATER))).toString() + " fl oz"
        }

        binding.lblBreastfeeding.text = requireContext().getString(R.string.breastfeeding)
        convertUpperCase(binding.lblBreastfeeding)
        binding.lblBreastfeeding.text = binding.lblBreastfeeding.getText().toString() + " (+" + bstr + ")"

        //====================================
        val tmp_weight = "" + SharedPreferencesManager.personWeight
        val isFemale: Boolean = SharedPreferencesManager.gender == 1
        val weatherIdx: Int = SharedPreferencesManager.climate

        var tmp_kg = 0.0f
        tmp_kg = if (SharedPreferencesManager.personWeightUnit) {
            ("" + tmp_weight).toFloat()
        } else {
            AppUtils.lbToKgConverter(tmp_weight.toDouble()).toFloat()
        }

        //====================
        var diff = 0.0f

        if (isFemale) diff = tmp_kg * AppUtils.DEACTIVE_FEMALE_WATER
        else diff = tmp_kg * AppUtils.DEACTIVE_MALE_WATER


        //====================
        diff *= when (weatherIdx) {
            1 -> AppUtils.WEATHER_CLOUDY
            2 -> AppUtils.WEATHER_RAINY
            3 -> AppUtils.WEATHER_SNOW
            else -> AppUtils.WEATHER_SUNNY
        }

        //====================
        bstr = ""

        bstr = if (SharedPreferencesManager.personWeightUnit) {
            Math.round(diff).toString() + " ml"
        } else {
            (Math.round(AppUtils.mlToOzUS(diff))).toString() + " fl oz"
        }

        binding.lblActive.text = requireContext().getString(R.string.active)
        convertUpperCase(binding.lblActive)
        binding.lblActive.text = binding.lblActive.getText().toString() + " (+" + bstr + ")"
    }

    private fun saveData(txt_name: AppCompatEditText) {
        d("saveData", "" + txt_name.text.toString().trim { it <= ' ' })

        SharedPreferencesManager.personHeight =
            "" + txt_name.text.toString().trim { it <= ' ' }

        SharedPreferencesManager.setManuallyGoal = false
    }

    private fun saveWeightData(txt_name: AppCompatEditText) {

        SharedPreferencesManager.personWeight =
            "" + txt_name.text.toString().trim { it <= ' ' }

        SharedPreferencesManager.setManuallyGoal = false

        refreshWidget()
    }

    private fun refreshWidget() {
        val intent = Intent(requireActivity(), NewAppWidget::class.java)
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        val ids = AppWidgetManager.getInstance(requireActivity()).getAppWidgetIds(
            ComponentName(
                requireActivity(),
                NewAppWidget::class.java
            )
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        requireActivity().sendBroadcast(intent)
    }

    //===============
    fun init_WeightKG() {
        weight_kg_lst.clear()
        var f = 30.0f
        weight_kg_lst.add("" + f)
        for (k in 0..199) {
            f += 0.5.toFloat()
            weight_kg_lst.add("" + f)
        }

        val st = arrayOfNulls<CharSequence>(weight_kg_lst.size)
        for (k in weight_kg_lst.indices) {
            st[k] = "" + weight_kg_lst[k]
        }
    }

    fun init_WeightLB() {
        weight_lb_lst.clear()
        for (k in 66..287) {
            weight_lb_lst.add("" + k)
        }

        val st = arrayOfNulls<CharSequence>(weight_lb_lst.size)
        for (k in weight_lb_lst.indices) {
            st[k] = "" + weight_lb_lst[k]
        }
    }

    //===============
    fun init_HeightCM() {
        height_cm_lst.clear()
        for (k in 60..240) {
            height_cm_lst.add("" + k)
        }

        val st = arrayOfNulls<CharSequence>(height_cm_lst.size)
        for (k in height_cm_lst.indices) {
            st[k] = "" + height_cm_lst[k]
        }
    }

    fun init_HeightFeet() {
        height_feet_lst.clear()
        height_feet_lst.add("2.0")
        height_feet_lst.add("2.1")
        height_feet_lst.add("2.2")
        height_feet_lst.add("2.3")
        height_feet_lst.add("2.4")
        height_feet_lst.add("2.5")
        height_feet_lst.add("2.6")
        height_feet_lst.add("2.7")
        height_feet_lst.add("2.8")
        height_feet_lst.add("2.9")
        height_feet_lst.add("2.10")
        height_feet_lst.add("2.11")
        height_feet_lst.add("3.0")
        height_feet_lst.add("3.1")
        height_feet_lst.add("3.2")
        height_feet_lst.add("3.3")
        height_feet_lst.add("3.4")
        height_feet_lst.add("3.5")
        height_feet_lst.add("3.6")
        height_feet_lst.add("3.7")
        height_feet_lst.add("3.8")
        height_feet_lst.add("3.9")
        height_feet_lst.add("3.10")
        height_feet_lst.add("3.11")
        height_feet_lst.add("4.0")
        height_feet_lst.add("4.1")
        height_feet_lst.add("4.2")
        height_feet_lst.add("4.3")
        height_feet_lst.add("4.4")
        height_feet_lst.add("4.5")
        height_feet_lst.add("4.6")
        height_feet_lst.add("4.7")
        height_feet_lst.add("4.8")
        height_feet_lst.add("4.9")
        height_feet_lst.add("4.10")
        height_feet_lst.add("4.11")
        height_feet_lst.add("5.0")
        height_feet_lst.add("5.1")
        height_feet_lst.add("5.2")
        height_feet_lst.add("5.3")
        height_feet_lst.add("5.4")
        height_feet_lst.add("5.5")
        height_feet_lst.add("5.6")
        height_feet_lst.add("5.7")
        height_feet_lst.add("5.8")
        height_feet_lst.add("5.9")
        height_feet_lst.add("5.10")
        height_feet_lst.add("5.11")
        height_feet_lst.add("6.0")
        height_feet_lst.add("6.1")
        height_feet_lst.add("6.2")
        height_feet_lst.add("6.3")
        height_feet_lst.add("6.4")
        height_feet_lst.add("6.5")
        height_feet_lst.add("6.6")
        height_feet_lst.add("6.7")
        height_feet_lst.add("6.8")
        height_feet_lst.add("6.9")
        height_feet_lst.add("6.10")
        height_feet_lst.add("6.11")
        height_feet_lst.add("7.0")
        height_feet_lst.add("7.1")
        height_feet_lst.add("7.2")
        height_feet_lst.add("7.3")
        height_feet_lst.add("7.4")
        height_feet_lst.add("7.5")
        height_feet_lst.add("7.6")
        height_feet_lst.add("7.7")
        height_feet_lst.add("7.8")
        height_feet_lst.add("7.9")
        height_feet_lst.add("7.10")
        height_feet_lst.add("7.11")
        height_feet_lst.add("8.0")

        val st = arrayOfNulls<CharSequence>(height_feet_lst.size)
        for (k in height_feet_lst.indices) {
            st[k] = "" + height_feet_lst[k]
        }
    }

}