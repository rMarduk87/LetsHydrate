package rpt.tool.mementobibere.ui.drink

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.skydoves.balloon.BalloonAlign
import com.skydoves.balloon.balloon
import github.com.st235.lib_expandablebottombar.Menu
import github.com.st235.lib_expandablebottombar.MenuItem
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.InitUserInfoActivity
import rpt.tool.mementobibere.MainActivity
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.StatsBaseActivity
import rpt.tool.mementobibere.WalkThroughActivity
import rpt.tool.mementobibere.databinding.DrinkFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.balloon.blood.BloodDonorInfoBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.FirstHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.FourthHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.SecondHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.ThirdHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.FifthHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.SixthHelpBalloonFactory
import rpt.tool.mementobibere.utils.balloon.tutorial.SeventhHelpBalloonFactory
import rpt.tool.mementobibere.utils.extensions.toCalculatedValue
import rpt.tool.mementobibere.utils.extensions.toExtractFloat
import rpt.tool.mementobibere.utils.extensions.toMainTheme
import rpt.tool.mementobibere.utils.extensions.toNumberString
import rpt.tool.mementobibere.utils.extensions.toStringHour
import rpt.tool.mementobibere.utils.helpers.AlarmHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class DrinkFragment : BaseFragment<DrinkFragmentBinding>(DrinkFragmentBinding::inflate) {

    private var start: Boolean = false
    private var refreshed: Boolean = false
    private var clicked: Int = 0
    private var counter: Int = 0
    private var enabled: Boolean = true
    private lateinit var unit: String
    private lateinit var menuNotify: Menu
    private lateinit var menuNotNotify: Menu
    private lateinit var outValue: TypedValue
    private lateinit var viewWindow: View
    private var totalIntake: Float = 0f
    private var inTook: Float = 0f
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var dateNow: String
    private var notificStatus: Boolean = false
    private var selectedOption: Float? = null
    private var snackbar: Snackbar? = null
    private var themeInt: Int = 0
    private var current_unitInt: Int = 0
    private var new_unitInt: Int = 0
    private var value_50: Float = 50f
    private var value_100: Float = 100f
    private var value_150: Float = 150f
    private var value_200: Float = 200f
    private var value_250: Float = 250f
    private var value_300: Float = 300f
    private var btnSelected: Int? = null
    private var intookToRefresh: Float = 0f
    private var waters: Array<String> = arrayOf()
    private val avisBalloon by balloon<BloodDonorInfoBalloonFactory>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        current_unitInt = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        new_unitInt = sharedPref.getInt(AppUtils.UNIT_NEW_KEY,0)
        value_50 = sharedPref.getFloat(AppUtils.VALUE_50_KEY,50f)
        value_100 = sharedPref.getFloat(AppUtils.VALUE_100_KEY,100f)
        value_150 = sharedPref.getFloat(AppUtils.VALUE_150_KEY,150f)
        value_200 = sharedPref.getFloat(AppUtils.VALUE_200_KEY,200f)
        value_250 = sharedPref.getFloat(AppUtils.VALUE_250_KEY,250f)
        value_300 = sharedPref.getFloat(AppUtils.VALUE_300_KEY,300f)
        sqliteHelper = SqliteHelper(requireContext())
        dateNow = AppUtils.getCurrentOnlyDate()!!
        waters = requireContext().resources.getStringArray(R.array.water)
        setTheme()
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setBackGround()

        if(sharedPref.getInt(AppUtils.BLOOD_DONOR_KEY,0)==1 &&
            !sqliteHelper.getAvisDay(dateNow)){
            binding.calendarAvis.visibility = VISIBLE
            binding.calendarAvisHelp!!.visibility = VISIBLE
        }
        else if(sharedPref.getInt(AppUtils.BLOOD_DONOR_KEY,0)==1 &&
            sqliteHelper.getAvisDay(dateNow)){
            binding.calendarAvis.visibility = VISIBLE
            binding.infoAvis!!.visibility = VISIBLE
            binding.calendarAvisHelp!!.visibility = VISIBLE
        }
        else{
            binding.calendarAvis.visibility = GONE
            binding.infoAvis!!.visibility = GONE
            binding.calendarAvisHelp!!.visibility = GONE
        }

        totalIntake = try {
            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
                .toCalculatedValue(current_unitInt,new_unitInt)
        }catch (ex:Exception){
            var totalIntakeOld = sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY,0)
            var editor = sharedPref.edit()
            editor.remove(AppUtils.TOTAL_INTAKE_KEY)
            editor.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntakeOld.toFloat())
            editor.apply()
            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
                .toCalculatedValue(current_unitInt,new_unitInt)
        }
        if (sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, true)) {
            startActivity(Intent(requireContext(), WalkThroughActivity::class.java))
        } else if (totalIntake <= 0) {
            startActivity(Intent(requireContext(), InitUserInfoActivity::class.java))
        }

        viewWindow = requireActivity().window.decorView.findViewById<View>(android.R.id.content)
        initBottomBar()
        if (!sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, true) || totalIntake > 0) {
            initIntookValue()
            setValueForDrinking()
        }
    }

    private fun initIntookValue() {
        unit = AppUtils.calculateExtensions(current_unitInt)
        binding.ml50!!.text = "${value_50.toNumberString()} $unit"
        binding.ml100!!.text = "${value_100.toNumberString()} $unit"
        binding.ml150!!.text = "${value_150.toNumberString()} $unit"
        binding.ml200!!.text = "${value_200.toNumberString()} $unit"
        binding.ml250!!.text = "${value_250.toNumberString()} $unit"
        binding.ml300!!.text = "${value_300.toNumberString()} $unit"
    }

    private fun initBottomBar() {

        menuNotify = binding.bottomBarNotify.menu
        menuNotNotify = binding.bottomBarNotNotify.menu

        createMenu()

    }

    private fun createMenu() {

        var colorString = if(themeInt==0){
            "#41B279"
        }
        else if(themeInt==1){
            "#29704D"
        }
        else{
            "#4167B2"
        }

        for (i in AppUtils.listIds.indices) {
            menuNotify.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIds[i],
                    AppUtils.listIconNotify[i],
                    AppUtils.listStringNotify[i],
                    Color.parseColor(colorString)
                )
                    .build()
            )
        }

        for (i in AppUtils.listIds.indices) {
            menuNotNotify.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIds[i],
                    AppUtils.listIconNotNotify[i],
                    AppUtils.listStringNotNotify[i],
                    Color.parseColor(colorString)
                )
                    .build()
            )
        }

        binding.bottomBarNotify.onItemSelectedListener = { _, i, _ ->
            manageListeners(i)


        }

        binding.bottomBarNotify.onItemReselectedListener = { _, i, _ ->
            manageListeners(i)
        }

        binding.bottomBarNotNotify.onItemSelectedListener = { _, i, _ ->
            manageListeners(i)

        }

        binding.bottomBarNotNotify.onItemReselectedListener = { _, i, _ ->
            manageListeners(i)
        }
    }

    private fun manageListeners(i: MenuItem) {
        when(i.id) {
            R.id.icon_bell -> manageNotification()
            R.id.icon_edit -> goToBottomEdit()
            R.id.icon_other -> goToBottomInfo()
            R.id.icon_stats -> goToStatsActivity()
        }
    }

    private fun goToStatsActivity() {
        Handler(requireContext().mainLooper).postDelayed({
            var edit = sharedPref.edit()
            edit.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntake)
            edit.putString(AppUtils.UNIT_STRING,unit)
            edit.apply()
            startActivity(Intent(requireActivity(), StatsBaseActivity::class.java))

        }, TIME)

    }

    private fun goToBottomInfo() {
        Handler(requireContext().mainLooper).postDelayed({

            var edit = sharedPref.edit()
            edit.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntake)
            edit.apply()
            safeNavController?.safeNavigate(
                DrinkFragmentDirections.actionDrinkFragmentToInfoFragment())

        }, TIME)

    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun addDrinkedWater() {

        if (selectedOption != null) {
            if ((inTook * 100 / totalIntake) <= 140 && clicked <= 1) {
                if (sqliteHelper.addIntook(dateNow, selectedOption!!,unit) > 0) {
                    inTook += selectedOption!!
                    setWaterLevel(inTook, totalIntake)
                    showMessage(getString(R.string.your_water_intake_was_saved),viewWindow,false,
                        AppUtils.Companion.TypeMessage.SAVE)
                    sqliteHelper.addOrUpdateIntookCounter(dateNow,btnSelected!!.toFloat(), 1)
                    addLastIntook(btnSelected!!.toFloat())
                }
            } else {
                binding.intakeProgress.labelText = "${
                    getString(
                        R.string.you_achieved_the_goal
                    )}"
                showMessage(getString(R.string.you_already_achieved_the_goal), viewWindow)
            }
            binding.tvCustom.text = requireContext().getText(R.string.custom)
            binding.op50ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op100ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op150ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op200ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op250ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op300ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.opCustom.background = requireContext().getDrawable(outValue.resourceId)
            binding.opDrinkAll.background = requireContext().getDrawable(outValue.resourceId)
            binding.opScan.background = requireContext().getDrawable(outValue.resourceId)

            // remove pending notifications
            val mNotificationManager : NotificationManager = requireActivity()
                .getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancelAll()
        }
    }

    private fun addLastIntook(toFloat: Float) {
        var edit = sharedPref.edit()
        edit.putFloat(AppUtils.LAST_INTOOK_KEY,toFloat)
        edit.apply()
    }

    private fun goToBottomEdit() {
        var editor = sharedPref.edit()
        editor.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntake)
        editor.apply()
        Handler(requireContext().mainLooper).postDelayed({

            safeNavController?.safeNavigate(DrinkFragmentDirections
                .actionDrinkFragmentToEditInfoBottomSheetFragment())

        }, TIME)
    }

    private fun manageNotification() {
        val alarm = AlarmHelper()
        if(enabled){
            notificStatus = !notificStatus
            var editor = sharedPref.edit()
            editor.putBoolean(AppUtils.NOTIFICATION_STATUS_KEY, notificStatus)
            editor.apply()
            if (notificStatus) {
                binding.bottomBarNotNotify.visibility = View.GONE
                binding.bottomBarNotify.visibility = View.VISIBLE
                Snackbar.make(viewWindow, getString(R.string.notification_enabled), Snackbar.LENGTH_SHORT).show()
                alarm.setAlarm(
                    requireContext(),
                    sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong())
            } else {
                binding.bottomBarNotNotify.visibility = View.VISIBLE
                binding.bottomBarNotify.visibility = View.GONE
                Snackbar.make(viewWindow, getString(R.string.notification_disabled), Snackbar.LENGTH_SHORT).show()
                alarm.cancelAlarm(requireContext())
            }
        }
    }

    private fun setBackGround() {
        when(themeInt){
            0->toLightTheme()
            1->toDarkTheme()
            2->toWaterTheme()
        }
    }

    private fun toWaterTheme() {
        binding.mainActivityParent.background = requireContext().getDrawable(R.drawable.ic_app_bg_w)
        if(sqliteHelper.getAvisDay(dateNow)){
            binding.tvIntook.setTextColor(resources.getColor(R.color.red))
            binding.tvTotalIntake.setTextColor(resources.getColor(R.color.red))
        }
        else{
            binding.tvIntook.setTextColor(requireContext().getColor(R.color.colorWhite))
            binding.tvTotalIntake.setTextColor(requireContext().getColor(R.color.colorWhite))
        }
        binding.bottomBarNotify.setBackgroundColorRes(R.color.colorWhite)
        binding.bottomBarNotNotify.setBackgroundColorRes(R.color.colorWhite)
        binding.intakeProgress.colorBackground = requireContext().getColor(R.color.colorSecondaryLightW)
    }

    private fun toDarkTheme() {
        binding.mainActivityParent.background = requireContext().getDrawable(R.drawable.ic_app_bg_dark)
        if(sqliteHelper.getAvisDay(dateNow)){
            binding.tvIntook.setTextColor(resources.getColor(R.color.red))
            binding.tvTotalIntake.setTextColor(resources.getColor(R.color.red))
        }
        else{
            binding.tvIntook.setTextColor(requireContext().getColor(R.color.colorBlack))
            binding.tvTotalIntake.setTextColor(requireContext().getColor(R.color.colorBlack))
        }
        binding.intakeProgress.colorBackground = requireContext().getColor(R.color.darkGreen)
    }

    private fun toLightTheme() {
        binding.mainActivityParent.background = requireContext().getDrawable(R.drawable.ic_app_bg)
        if(sqliteHelper.getAvisDay(dateNow)){
            binding.tvIntook.setTextColor(resources.getColor(R.color.red))
            binding.tvTotalIntake.setTextColor(resources.getColor(R.color.red))
        }
        else{
            binding.tvIntook.setTextColor(requireContext().getColor(R.color.colorWhite))
            binding.tvTotalIntake.setTextColor(requireContext().getColor(R.color.colorWhite))
        }
        binding.intakeProgress.colorBackground = requireContext().getColor(R.color.teal_700)
    }

    private fun setTheme() {
        val theme = themeInt.toMainTheme()
        requireActivity().setTheme(theme)

    }

    private fun updateValues() {
        totalIntake = try {
            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
        }catch (ex:Exception){
            var totalIntakeOld = sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY,0)
            var editor = sharedPref.edit()
            editor.remove(AppUtils.TOTAL_INTAKE_KEY)
            editor.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntakeOld.toFloat())
            editor.apply()
            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
        }

        var noUpdate = sharedPref.getBoolean(AppUtils.NO_UPDATE_UNIT,false)
        if(!noUpdate){
            totalIntake = sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
                .toCalculatedValue(current_unitInt,calculateRealUnit(current_unitInt,new_unitInt))

            unit = AppUtils.calculateExtensions(new_unitInt)
            sqliteHelper.updateTotalIntake(
                AppUtils.getCurrentOnlyDate()!!,
                totalIntake, unit)
        }

        var editor = sharedPref.edit()
        editor.putBoolean(AppUtils.NO_UPDATE_UNIT, false)
        editor.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntake)
        editor.apply()

        sqliteHelper.addAll(dateNow, 0, totalIntake,unit, themeInt)

        inTook = sqliteHelper.getIntook(dateNow)

        setWaterLevel(inTook, totalIntake)
    }

    private fun calculateRealUnit(currentUnitint: Int, newUnitint: Int): Int {
        val realUnit = new_unitInt
        if(currentUnitint==newUnitint){
            val c = sqliteHelper.getTotalIntake(dateNow)
            if(c.moveToFirst()){
                val unit = AppUtils.extractIntConversion(c.getString(1))
                return if(unit==new_unitInt){
                    realUnit
                } else{
                    unit
                }
            }
        }
        return realUnit
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()

        setBackGround()

        if(sqliteHelper.getAvisDay(dateNow)){
            binding.tvIntook.setTextColor(resources.getColor(R.color.red))
            binding.tvTotalIntake.setTextColor(resources.getColor(R.color.red))
        }

        val new = sharedPref.getBoolean(AppUtils.RESET_NOTIFICATION_KEY,true)
        if(new){
            val mNotificationManager : NotificationManager = requireActivity()
                .getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotificationManager.deleteNotificationChannel("rpt.tools.mementobibere.CHANNELONE")
            }
            var edit = sharedPref.edit()
            edit.putBoolean(AppUtils.RESET_NOTIFICATION_KEY,false)
            edit.apply()
        }

        if(!sharedPref.getBoolean(AppUtils.SET_GENDER_KEY, false)){
            setGender()
        }

        if(!sharedPref.getBoolean(AppUtils.SET_BLOOD_KEY, false)){
            setBloodDonor()
        }

        if(!sharedPref.getBoolean(AppUtils.SET_NEW_WORK_TYPE_KEY, false)){
            setNewWorkType()
        }

        if(!sharedPref.getBoolean(AppUtils.SET_CLIMATE_KEY, false)){
            setClimate()
        }

        totalIntake = try {
            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
                .toCalculatedValue(current_unitInt,new_unitInt)
        }catch (ex:Exception){
            var totalIntakeOld = sharedPref.getInt(AppUtils.TOTAL_INTAKE_KEY,0)
            var editor = sharedPref.edit()
            editor.remove(AppUtils.TOTAL_INTAKE_KEY)
            editor.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntakeOld.toFloat())
            editor.apply()
            sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
                .toCalculatedValue(current_unitInt,new_unitInt)
        }

        setValueForDrinking()

        outValue = TypedValue()
        requireContext().applicationContext.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )

        if(!sharedPref.getBoolean(AppUtils.SET_WEIGHT_UNIT, false)){
            safeNavController?.safeNavigate(DrinkFragmentDirections.actionDrinkFragmentToSelectWeightBottomSheetFragment())
        }

        if(!AppUtils.isValidDate(
                Date(sharedPref.getLong(AppUtils.SLEEPING_TIME_KEY,0L)).toStringHour(),
                Date(sharedPref.getLong(AppUtils.WAKEUP_TIME_KEY,0L)).toStringHour())){
            safeNavController?.safeNavigate(DrinkFragmentDirections.actionDrinkFragmentToAdjustHourBottomSheetFragment())
        }

        notificStatus = sharedPref.getBoolean(AppUtils.NOTIFICATION_STATUS_KEY, true)
        val alarm = AlarmHelper()
        if (!alarm.checkAlarm(requireContext()) && notificStatus) {
            binding.bottomBarNotNotify.visibility = View.GONE
            binding.bottomBarNotify.visibility = View.VISIBLE
            var isAvisDay = sqliteHelper.getAvisDay(dateNow)
            var freq = sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
            if(isAvisDay){
                freq /= 2
            }
            alarm.setAlarm(
                requireContext(),freq
            )
        }

        if (notificStatus) {
            binding.bottomBarNotNotify.visibility = VISIBLE
            binding.bottomBarNotify.visibility = GONE
        }

        if (!notificStatus) {
            binding.bottomBarNotNotify.visibility = GONE
            binding.bottomBarNotify.visibility = VISIBLE
        }

        current_unitInt = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        new_unitInt = sharedPref.getInt(AppUtils.UNIT_NEW_KEY,0)

        updateValues()

        var background = if(themeInt!=2){
            R.drawable.option_select_bg
        }
        else{
            R.drawable.option_select_bg_w
        }

        if(sharedPref.getBoolean(AppUtils.SEE_TIPS_KEY,true)){
            if(inTook > 0){
                binding.bubble.visibility = VISIBLE
                binding.se.visibility = VISIBLE
                val randomIndex: Int = java.util.Random().nextInt(waters.size)
                val randomWaters: String = waters[randomIndex]
                binding.se.text = randomWaters
            }
            else{
                binding.bubble.visibility = INVISIBLE
                binding.se.visibility = INVISIBLE
            }
        }

        binding.op50ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 0
            selectedOption = binding.ml50!!.text.toString().toExtractFloat()
            binding.op50ml.background = requireContext().getDrawable(background)
            binding.op100ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op150ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op200ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op250ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op300ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.opCustom.background = requireContext().getDrawable(outValue.resourceId)
            binding.opDrinkAll.background = requireContext().getDrawable(outValue.resourceId)
            binding.opScan.background = requireContext().getDrawable(outValue.resourceId)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.op100ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 1
            selectedOption = binding.ml100!!.text.toString().toExtractFloat()
            binding.op50ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op100ml.background = requireContext().getDrawable(background)
            binding.op150ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op200ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op250ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op300ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.opCustom.background = requireContext().getDrawable(outValue.resourceId)
            binding.opDrinkAll.background = requireContext().getDrawable(outValue.resourceId)
            binding.opScan.background = requireContext().getDrawable(outValue.resourceId)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.op150ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 2
            selectedOption = binding.ml150!!.text.toString().toExtractFloat()
            binding.op50ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op100ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op150ml.background = requireContext().getDrawable(background)
            binding.op200ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op250ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op300ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.opCustom.background = requireContext().getDrawable(outValue.resourceId)
            binding.opDrinkAll.background = requireContext().getDrawable(outValue.resourceId)
            binding.opScan.background = requireContext().getDrawable(outValue.resourceId)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.op200ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 3
            selectedOption = binding.ml200!!.text.toString().toExtractFloat()
            binding.op50ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op100ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op150ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op200ml.background = requireContext().getDrawable(background)
            binding.op250ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op300ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.opCustom.background = requireContext().getDrawable(outValue.resourceId)
            binding.opDrinkAll.background = requireContext().getDrawable(outValue.resourceId)
            binding.opScan.background = requireContext().getDrawable(outValue.resourceId)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.op250ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 4
            selectedOption = binding.ml250!!.text.toString().toExtractFloat()
            binding.op50ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op100ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op150ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op200ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op250ml.background = requireContext().getDrawable(background)
            binding.op300ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.opCustom.background = requireContext().getDrawable(outValue.resourceId)
            binding.opDrinkAll.background = requireContext().getDrawable(outValue.resourceId)
            binding.opScan.background = requireContext().getDrawable(outValue.resourceId)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.op300ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 5
            selectedOption = binding.ml300!!.text.toString().toExtractFloat()
            binding.op50ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op100ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op150ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op200ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op250ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op300ml.background = requireContext().getDrawable(background)
            binding.opCustom.background = requireContext().getDrawable(outValue.resourceId)
            binding.opDrinkAll.background = requireContext().getDrawable(outValue.resourceId)
            binding.opScan.background = requireContext().getDrawable(outValue.resourceId)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.opCustom.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }

            val li = LayoutInflater.from(requireContext())
            val promptsView = li.inflate(R.layout.custom_input_dialog, null)

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(promptsView)

            val userInput = promptsView
                .findViewById(R.id.etCustomInput) as TextInputLayout

            alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                val inputText = userInput.editText!!.text.toString()
                if (!TextUtils.isEmpty(inputText)) {
                    binding.tvCustom.text = "$inputText $unit"
                    selectedOption = binding.tvCustom.text.toString().toExtractFloat()
                    btnSelected = 6
                    addDrinkedWater()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            binding.op50ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op100ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op150ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op200ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op250ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op300ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.opCustom.background = requireContext().getDrawable(background)
            binding.opDrinkAll.background = requireContext().getDrawable(outValue.resourceId)
            binding.opScan.background = requireContext().getDrawable(outValue.resourceId)
        }

        binding.opDrinkAll.setOnClickListener {
            if(clicked < 1 && inTook < totalIntake){
                if (snackbar != null) {
                    snackbar?.dismiss()
                }
                clicked += 1
                btnSelected = 6
                selectedOption = AppUtils.CalculateOption(inTook,totalIntake)!!
                binding.op50ml.background = requireContext().getDrawable(outValue.resourceId)
                binding.op100ml.background = requireContext().getDrawable(outValue.resourceId)
                binding.op150ml.background = requireContext().getDrawable(outValue.resourceId)
                binding.op200ml.background = requireContext().getDrawable(outValue.resourceId)
                binding.op250ml.background = requireContext().getDrawable(outValue.resourceId)
                binding.op300ml.background = requireContext().getDrawable(outValue.resourceId)
                binding.opCustom.background = requireContext().getDrawable(outValue.resourceId)
                binding.opDrinkAll.background = requireContext().getDrawable(background)
                binding.opScan.background = requireContext().getDrawable(outValue.resourceId)
                addDrinkedWater()
            }
            else{
                showMessage(getString(R.string.option_selectable_once_a_day),it)
            }
        }

        binding.opScan.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }

            val li = LayoutInflater.from(requireContext())
            val promptsView = li.inflate(R.layout.custom_input_dialog3, null)

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(promptsView)

            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                binding.opScan.background = requireContext().getDrawable(outValue.resourceId)
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            binding.op50ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op100ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op150ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op200ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op250ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.op300ml.background = requireContext().getDrawable(outValue.resourceId)
            binding.opCustom.background = requireContext().getDrawable(outValue.resourceId)
            binding.opDrinkAll.background = requireContext().getDrawable(outValue.resourceId)
            binding.opScan.background = requireContext().getDrawable(background)

        }


        if (!sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, true) || totalIntake > 0) {
            setValueForDrinking()
        }


        binding.btnUndo.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            undoLastDailyIntook()
        }

        binding.btnRedo.setOnClickListener {
            restoreDailyIntook()
        }

        binding.btnRefresh.setOnClickListener {
            resetDailyIntook()
        }

        binding.calendarAvisHelp!!.setOnClickListener {
            avisBalloon.showAlign(
                align = BalloonAlign.BOTTOM,
                mainAnchor = binding.calendarAvis as View,
                subAnchorList = listOf(it),
            )
        }

        binding.calendarAvis.setOnClickListener{
            val calendar = Calendar.getInstance()

            val mDatePicker = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    var calendarPre = calendar
                    calendarPre.add(Calendar.DAY_OF_MONTH, -1)

                    val myFormat = "dd-MM-yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat)
                    val preAvis = sdf.format(calendarPre.time)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    val avis = sdf.format(calendar.time)
                    sqliteHelper.addAvis(preAvis)
                    sqliteHelper.addAvis(avis)
                    if(sdf.format(calendar.time) == dateNow){
                        if(totalIntake < 2000){
                            val editor = sharedPref.edit()
                            editor.putFloat(AppUtils.TOTAL_INTAKE_KEY,2000f)
                            editor.apply()
                        }
                    }
                    safeNavController?.safeNavigate(DrinkFragmentDirections
                        .actionDrinkFragmentToSelfFragment())
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            mDatePicker.datePicker.minDate = AppUtils.getMinDate()
            mDatePicker.setTitle("")
            mDatePicker.show()
        }

        binding.infoAvis!!.setOnClickListener {
            showMessage(
                getString(R.string.tomorrow_you_will_donate),it, duration = 3500)
        }

        binding.tutorial!!.setOnClickListener {
            safeNavController?.safeNavigate(DrinkFragmentDirections.
                actionDrinkFragmentToTutorialFragment())
        }

        if ((inTook * 100 / totalIntake) > 140) {
            binding.intakeProgress.labelText = "${
                getString(
                    R.string.you_achieved_the_goal
                )}"
        }

        if(sharedPref.getBoolean(AppUtils.START_TUTORIAL_KEY,false)){
            safeNavController?.safeNavigate(DrinkFragmentDirections
                .actionDrinkFragmentToTutorialFragment())
        }
    }

    private fun randomizeBalloon() {
        if(sharedPref.getBoolean(AppUtils.SEE_TIPS_KEY,true)){
            if(binding.bubble.visibility == INVISIBLE){
                binding.bubble.visibility = VISIBLE
                binding.se.visibility = VISIBLE
            }

            val randomIndex: Int = java.util.Random().nextInt(waters.size)
            val randomWaters: String = waters[randomIndex]
            binding.se.text = randomWaters
        }
    }

    private fun setBloodDonor() {
        var bloodDonorChoice = 0
        val li = LayoutInflater.from(requireContext())
        val promptsView = li.inflate(R.layout.custom_input_dialog_, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(promptsView)

        val userBlood = promptsView
            .findViewById(R.id.btnAvis) as LottieAnimationView

        userBlood.setOnClickListener {
            if(bloodDonorChoice==0){
                bloodDonorChoice = 1
                showMessage(getString(R.string.you_selected_avis),it)
            }
            else{
                bloodDonorChoice = 0
                showMessage(getString(R.string.you_selected_no_avis),it)
            }
        }

        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            val editor = sharedPref.edit()
            editor.putInt(AppUtils.BLOOD_DONOR_KEY, bloodDonorChoice)
            editor.putBoolean(AppUtils.SET_BLOOD_KEY,true)
            editor.apply()
        }.setNegativeButton("Cancel") { _, _ ->
            val editor = sharedPref.edit()
            editor.putInt(AppUtils.BLOOD_DONOR_KEY, bloodDonorChoice)
            editor.putBoolean(AppUtils.SET_BLOOD_KEY,true)
            editor.apply()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun setGender() {
        var genderChoice = -1
        val li = LayoutInflater.from(requireContext())
        val promptsView = li.inflate(R.layout.custom_input_dialog2, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(promptsView)

        val userMaleBtn = promptsView
            .findViewById(R.id.btnMan) as LottieAnimationView

        val userWomanBtn = promptsView
            .findViewById(R.id.btnWoman) as LottieAnimationView

        userMaleBtn.setOnClickListener {
            genderChoice = 0
            showMessage(getString(R.string.you_selected_man),it,
                type=AppUtils.Companion.TypeMessage.MAN)
        }

        userWomanBtn.setOnClickListener {
            genderChoice = 1
            showMessage(getString(R.string.you_selected_woman),it,
                type=AppUtils.Companion.TypeMessage.WOMAN)
        }

        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            when (genderChoice) {
                -1 -> showMessage(getString(R.string.gender_hint),promptsView,true)
                else -> {

                    val editor = sharedPref.edit()
                    editor.putInt(AppUtils.GENDER_KEY, genderChoice)
                    editor.putBoolean(AppUtils.SET_GENDER_KEY,true)
                    editor.apply()
                }
            }
        }.setNegativeButton("Cancel") { _, _ ->
            showMessage(getString(R.string.gender_hint),promptsView,true)
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun setNewWorkType(){
        var workType = -1
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
            when (workType) {
                -1 -> showMessage(getString(R.string.work_type_hint),promptsView,true)
                else -> {

                    val editor = sharedPref.edit()
                    editor.putInt(AppUtils.WORK_TIME_KEY, workType)
                    editor.putBoolean(AppUtils.SET_NEW_WORK_TYPE_KEY,true)
                    editor.apply()
                }
            }
        }.setNegativeButton("Cancel") { _, _ ->
            showMessage(getString(R.string.work_type_hint),promptsView,true)
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun setClimate(){
        var climate = -1
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
            when (climate) {
                -1 -> showMessage(getString(R.string.climate_set_hint),promptsView,true)
                else -> {

                    val editor = sharedPref.edit()
                    editor.putInt(AppUtils.CLIMATE_KEY, climate)
                    editor.putBoolean(AppUtils.SET_CLIMATE_KEY,true)
                    editor.apply()
                }
            }
        }.setNegativeButton("Cancel") { _, _ ->
            showMessage(getString(R.string.climate_set_hint),promptsView,true)
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun restoreDailyIntook() {
        selectedOption = if(refreshed){
            intookToRefresh
        }
        else{
            selectedOption
        }
        var totalIntook = sqliteHelper.getIntook(AppUtils.getCurrentDate()!!)
        if(selectedOption != null){
            sqliteHelper.resetIntook(AppUtils.getCurrentDate()!!)
            sqliteHelper.addIntook(AppUtils.getCurrentDate()!!,(totalIntook + selectedOption!!),unit)
            sqliteHelper.addOrUpdateIntookCounter(dateNow,btnSelected!!.toFloat(), 1)
            updateValues()
            addLastIntook(AppUtils.convertToSelected(selectedOption!!,unit))
            selectedOption = null
            counter = 0
            refreshed = false
            intookToRefresh = 0f
        }
    }

    private fun undoLastDailyIntook() {
        var totalIntook = sqliteHelper.getIntook(dateNow)
        if(selectedOption != null && counter == 0) {
            sqliteHelper.resetIntook(dateNow)
            sqliteHelper.addIntook(
                AppUtils.getCurrentDate()!!,
                (totalIntook - selectedOption!!),
                unit
            )
            addLastIntook(-1f)
            sqliteHelper.addOrUpdateIntookCounter(dateNow,btnSelected!!.toFloat(), -1)
            updateValues()
            sqliteHelper.removeReachedGoal(dateNow)
            counter = 1
            refreshed = false
        }
    }

    private fun resetDailyIntook() {
        var totalIntook = sqliteHelper.getIntook(dateNow)
        intookToRefresh = inTook
        if(totalIntook > 0){
            sqliteHelper.resetIntook(dateNow)
            updateValues()
            selectedOption = null
            sqliteHelper.resetIntookCounter(dateNow)
            sqliteHelper.removeReachedGoal(dateNow)
            addLastIntook(-1f)
            counter = 0
            binding.intakeProgress.labelText = "0%"
            clicked = 0
            refreshed = true
        }
    }

    override fun onResume() {
        super.onResume()

        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        current_unitInt = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        new_unitInt = sharedPref.getInt(AppUtils.UNIT_NEW_KEY,0)
        setTheme()
        setBackGround()
        val editor = sharedPref.edit()
        editor.putInt(AppUtils.UNIT_KEY,new_unitInt)
        editor.apply()
        refreshAlarm(sharedPref.getBoolean(AppUtils.NOTIFICATION_STATUS_KEY,true))
    }


    private fun setWaterLevel(inTook: Float, totalIntake: Float) {

        YoYo.with(Techniques.SlideInDown)
            .duration(500)
            .playOn(binding.tvIntook)
        binding.tvIntook.text = "" + inTook.toNumberString()
        binding.tvTotalIntake.text = "/"+totalIntake.toNumberString()+ " " + "$unit"
        val progress = ((inTook / totalIntake) * 100).toInt()
        YoYo.with(Techniques.Pulse)
            .duration(500)
            .playOn(binding.intakeProgress)
        binding.intakeProgress.progress = progress.toFloat()
        if(progress <= 140){
            binding.intakeProgress.setOnProgressChangeListener { binding.intakeProgress.labelText = "${
                getString(
                    R.string.drink
                )} ${it.toInt()}%" }
            binding.intakeProgress.labelText = "${
                getString(
                    R.string.drink
                )} ${progress}%"
        }


        if ((inTook * 100 / totalIntake) > 140) {
            showMessage(getString(R.string.you_achieved_the_goal),binding.mainActivityParent)
            sqliteHelper.addReachedGoal(dateNow,inTook,unit)
        }
    }

    private fun setValueForDrinking() {
        unit = AppUtils.calculateExtensions(new_unitInt)
        value_50 = sharedPref.getFloat(AppUtils.VALUE_50_KEY,50f)
        value_100 = sharedPref.getFloat(AppUtils.VALUE_100_KEY,100f)
        value_150 = sharedPref.getFloat(AppUtils.VALUE_150_KEY,150f)
        value_200 = sharedPref.getFloat(AppUtils.VALUE_200_KEY,200f)
        value_250 = sharedPref.getFloat(AppUtils.VALUE_250_KEY,250f)
        value_300 = sharedPref.getFloat(AppUtils.VALUE_300_KEY,300f)
        binding.ml50!!.text = "${value_50.toNumberString()} $unit"
        binding.ml100!!.text = "${value_100.toNumberString()} $unit"
        binding.ml150!!.text = "${value_150.toNumberString()} $unit"
        binding.ml200!!.text = "${value_200.toNumberString()} $unit"
        binding.ml250!!.text = "${value_250.toNumberString()} $unit"
        binding.ml300!!.text = "${value_300.toNumberString()} $unit"
        binding.tvTotalIntake!!.text = "/" +totalIntake.toNumberString() + " " + "$unit"
    }

    companion object {
        const val TIME: Long = 150
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        var alarm = AlarmHelper()
        when (requestCode) {
            123 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    binding.bottomBarNotNotify.visibility = View.GONE
                    binding.bottomBarNotify.visibility = View.VISIBLE
                    Snackbar.make(viewWindow, getString(R.string.notification_enabled), Snackbar.LENGTH_SHORT).show()
                    alarm.setAlarm(
                        requireContext(),
                        sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong())

                } else {
                    binding.bottomBarNotNotify.visibility = View.VISIBLE
                    binding.bottomBarNotify.visibility = View.GONE
                    Snackbar.make(viewWindow, getString(R.string.notification_disabled), Snackbar.LENGTH_SHORT).show()
                    alarm.cancelAlarm(requireContext())
                }
            }
        }
    }

    private fun refreshAlarm(notify: Boolean){
        var alarm = AlarmHelper()
        if(notify){
            binding.bottomBarNotNotify.visibility = View.GONE
            binding.bottomBarNotify.visibility = View.VISIBLE
            alarm.setAlarm(
                requireContext(),
                sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong())
        }
        else{
            binding.bottomBarNotNotify.visibility = View.VISIBLE
            binding.bottomBarNotify.visibility = View.GONE
            alarm.cancelAlarm(requireContext())
            val activity: Activity? = activity
            if (activity != null && activity is MainActivity) {
                activity.initPermissions()
            }
        }
    }
}