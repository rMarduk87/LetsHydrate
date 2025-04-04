package rpt.tool.mementobibere.ui.userinfo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.FragmentInitUserInfoBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.helpers.AlertHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.managers.MigrationManager
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.view.adapters.InitUserInfoPagerAdapter

@Suppress("DEPRECATION")
class InitUserInfoFragment:
    BaseFragment<FragmentInitUserInfoBinding>(FragmentInitUserInfoBinding::inflate) {


    var initUserInfoPagerAdapter: InitUserInfoPagerAdapter? = null
    var current_page_idx: Int = 0
    var max_page: Int = 7
    var alertHelper: AlertHelper? = null
    var sqliteHelper: SqliteHelper? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.navigationBarColor =
            requireContext().resources.getColor(R.color.water_color)

        alertHelper = AlertHelper(requireContext())
        sqliteHelper = SqliteHelper(requireContext())

        body()
    }

    fun body() {
        showTypeDialog()
        initUserInfoPagerAdapter = InitUserInfoPagerAdapter(
            requireActivity().supportFragmentManager, requireContext())
        binding.viewPager.setAdapter(initUserInfoPagerAdapter)
        binding.viewPager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                current_page_idx = position

                if (position == 0) {
                    binding.btnBack.visibility = View.GONE
                    binding.space.visibility = View.GONE
                } else {
                    binding.btnBack.visibility = View.VISIBLE
                    binding.space.visibility = View.VISIBLE
                }

                if (position == max_page - 1) {
                    binding.lblNext.text = requireActivity().getString(R.string.str_get_started)
                } else {
                    binding.lblNext.text = requireActivity().getString(R.string.str_next)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.viewPager.setOffscreenPageLimit(10)

        binding.dotsIndicator.setViewPager(binding.viewPager)

        binding.btnBack.setOnClickListener {
            if (current_page_idx > 0) {
                current_page_idx -= 1
            }
            binding.viewPager.setCurrentItem(current_page_idx)
        }
        
        binding.btnNext.setOnClickListener(View.OnClickListener {
            if (current_page_idx == 0) {
                if (AppUtils.checkBlankData(SharedPreferencesManager.userName)) {
                    alertHelper!!.customAlert(requireActivity().getString(R.string.str_your_name_validation))
                    return@OnClickListener
                }

                if (SharedPreferencesManager.userName.length < 3) {
                    alertHelper!!.customAlert(requireActivity().getString(R.string.str_valid_name_validation))
                    return@OnClickListener
                }
            }
            if (current_page_idx == 1) {
                try {
                    if (AppUtils.checkBlankData(SharedPreferencesManager.personHeight)) {
                        alertHelper!!.customAlert(requireActivity().getString(R.string.str_height_validation))
                        return@OnClickListener
                    }

                    if (AppUtils.checkBlankData(SharedPreferencesManager.personWeight)) {
                        alertHelper!!.customAlert(requireActivity().getString(R.string.str_weight_validation))
                        return@OnClickListener
                    }

                    val `val` = (SharedPreferencesManager.personWeight).toFloat()
                    if (`val` < 2) {
                        alertHelper!!.customAlert(requireActivity().getString(R.string.str_height_validation))
                        return@OnClickListener
                    }

                    val val2 = (SharedPreferencesManager.personWeight).toFloat()
                    if (val2 < 30) {
                        alertHelper!!.customAlert(requireActivity().getString(R.string.str_weight_validation))
                        return@OnClickListener
                    }
                } catch (e: Exception) {
                    e.message?.let { it1 -> e(Throwable(e), it1) }
                }
            }

            if (current_page_idx == 5) {
                if (SharedPreferencesManager.wakeUpTime == 0L ||
                    SharedPreferencesManager.sleepingTime == 0L) {
                    alertHelper!!.customAlert(requireActivity()
                        .getString(R.string.str_from_to_invalid_validation))
                    return@OnClickListener
                } else if (SharedPreferencesManager.ignoreNextStep) {
                    alertHelper!!.customAlert(requireActivity()
                        .getString(R.string.str_from_to_invalid_validation))
                    return@OnClickListener
                }
            }
            if (current_page_idx < max_page - 1) {
                current_page_idx += 1
                binding.viewPager.setCurrentItem(current_page_idx)
            } else {
                checkStoragePermissions()
            }
        })
    }

    @SuppressLint("InflateParams")
    private fun showTypeDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val view: View = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_type,
            null, false)


        val btn_cancel = view.findViewById<RelativeLayout>(R.id.btn_cancel)
        val btn_add = view.findViewById<RelativeLayout>(R.id.btn_add)
        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)


        val rdo_water = view.findViewById<RadioButton>(R.id.rdo_water)
        val rdo_bmi = view.findViewById<RadioButton>(R.id.rdo_bmi)

        rdo_water.setOnClickListener {
            SharedPreferencesManager.isCheckBMI = false
            rdo_water.isClickable = false
            rdo_bmi.isClickable = true
        }

        rdo_bmi.setOnClickListener {
            SharedPreferencesManager.isCheckBMI = true
            rdo_water.isClickable = true
            rdo_bmi.isClickable = false
        }

        if (SharedPreferencesManager.isCheckBMI) {
            rdo_water.isChecked = false
            rdo_water.isClickable = true
            rdo_bmi.isClickable = false
        } else {
            rdo_bmi.isChecked = true
            rdo_water.isClickable = true
            rdo_bmi.isClickable = false
        }


        btn_cancel.setOnClickListener { dialog.cancel() }

        img_cancel.setOnClickListener { dialog.cancel() }


        btn_add.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)

        dialog.show()
    }


    private fun checkStoragePermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 3
            )
        } else {
            gotoHomeScreen()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            3 ->
                gotoHomeScreen()

        }
    }

    private fun gotoHomeScreen() {
        SharedPreferencesManager.hideWelcomeScreen = true
        if(SharedPreferencesManager.isMigration){
            sqliteHelper!!.checkReachedAndDelete(AppUtils.DAILY_WATER_VALUE,
                AppUtils.getCurrentOnlyDate()!!,AppUtils.WATER_UNIT_VALUE)
            SharedPreferencesManager.isMigration = false
        }
        else{
            SharedPreferencesManager.isMigration = false
        }

        if(SharedPreferencesManager.isCheckBMI){
            addBMIData()
            val migrationManager = MigrationManager()
            migrationManager.migrateFromPrevious()
        }
        else{
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }

    }

    private fun addBMIData() {
        val initialValues = ContentValues()

        initialValues.put(
            "date",
            "" + AppUtils.getCurrentDate(AppUtils.DATE_FORMAT)
        )

        if (SharedPreferencesManager.personWeightUnit) {
            initialValues.put("weight_kg", "" + SharedPreferencesManager.personWeight)
            initialValues.put(
                "weight_lb",
                "" + AppUtils.kgToLbConverter(SharedPreferencesManager.personWeight.toDouble())
            )
        } else {
            initialValues.put(
                "weight_kg",
                "" + AppUtils.lblToKg(SharedPreferencesManager.personWeight.toInt())
            )
            initialValues.put("weight_lb", "" + SharedPreferencesManager.personWeight)
        }

        val kg = (if (SharedPreferencesManager.personWeightUnit)
            SharedPreferencesManager.personWeight.toDouble() else
            AppUtils.lblToKg(SharedPreferencesManager.personWeight.toInt())).toDouble()

        val bmi = AppUtils.getBMIKg(SharedPreferencesManager.personHeight.toDouble(),kg)

        initialValues.put("bmi_index", "" + bmi)

        sqliteHelper!!.insert("bmi", initialValues)

        showBMIDialog(bmi)

    }

    @SuppressLint("InflateParams")
    private fun showBMIDialog(bmi: Double) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.drawable_background_tra)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val view: View = LayoutInflater.from(requireActivity())
            .inflate(R.layout.dialog_bmi, null, false)


        val img_cancel = view.findViewById<ImageView>(R.id.img_cancel)

        val lbl_bmi = view.findViewById<AppCompatTextView>(R.id.lbl_bmi)

        lbl_bmi.text = AppUtils.getBMICategory(requireContext(),bmi)

        img_cancel.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }


        dialog.setOnDismissListener { }

        dialog.setContentView(view)

        dialog.show()
    }

}