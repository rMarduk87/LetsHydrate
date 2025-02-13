package rpt.tool.mementobibere.ui.drink

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import rpt.tool.mementobibere.databinding.DrinkFragmentBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.balloon.blood.BloodDonorInfoBalloonFactory
import rpt.tool.mementobibere.utils.extensions.toExtractFloat
import rpt.tool.mementobibere.utils.extensions.toMainTheme
import rpt.tool.mementobibere.utils.extensions.toMonth
import rpt.tool.mementobibere.utils.extensions.toNumberString
import rpt.tool.mementobibere.utils.extensions.toStringHour
import rpt.tool.mementobibere.utils.extensions.toYear
import rpt.tool.mementobibere.utils.helpers.AlarmHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.managers.SharedPreferencesManager
import rpt.tool.mementobibere.utils.navigation.safeNavController
import rpt.tool.mementobibere.utils.navigation.safeNavigate
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class DrinkFragment : BaseFragment<DrinkFragmentBinding>(DrinkFragmentBinding::inflate) {

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
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var dateNow: String
    private var notificStatus: Boolean = false
    private var selectedOption: Float? = null
    private var snackbar: Snackbar? = null
    private var btnSelected: Int? = null
    private var intookToRefresh: Float = 0f
    private var waters: Array<String> = arrayOf()
    private val avisBalloon by balloon<BloodDonorInfoBalloonFactory>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sqliteHelper = SqliteHelper(requireContext())
        dateNow = AppUtils.getCurrentOnlyDate()!!
        waters = requireContext().resources.getStringArray(R.array.water)
        setTheme()
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setBackGround()

        if(SharedPreferencesManager.bloodDonorKey==1 &&
            !sqliteHelper.getAvisDay(dateNow)){
            binding.calendarAvis.visibility = VISIBLE
            binding.calendarAvisHelp.visibility = VISIBLE
        }
        else if(SharedPreferencesManager.bloodDonorKey==1 &&
            sqliteHelper.getAvisDay(dateNow)){
            binding.calendarAvis.visibility = VISIBLE
            binding.infoAvis.visibility = VISIBLE
            binding.calendarAvisHelp.visibility = VISIBLE
        }
        else{
            binding.calendarAvis.visibility = GONE
            binding.infoAvis.visibility = GONE
            binding.calendarAvisHelp.visibility = GONE
        }

        totalIntake = SharedPreferencesManager.totalIntake

         if (totalIntake <= 0) {
            startActivity(Intent(requireContext(), InitUserInfoActivity::class.java))
            requireActivity().finish()
        }

        viewWindow = requireActivity().window.decorView.findViewById<View>(android.R.id.content)
        initBottomBar()
        if (!SharedPreferencesManager.firstRun || totalIntake > 0) {
            SharedPreferencesManager.value_300 =  AppUtils.firstConversion(300f,
                SharedPreferencesManager.new_unitInt)
            initIntookValue()
            setValueForDrinking()
        }
    }

    private fun initIntookValue() {
        unit = AppUtils.calculateExtensions(SharedPreferencesManager.new_unitInt)
        binding.ml50!!.text = "${SharedPreferencesManager.value_50.toNumberString()} $unit"
        binding.ml100!!.text = "${SharedPreferencesManager.value_100.toNumberString()} $unit"
        binding.ml150!!.text = "${SharedPreferencesManager.value_150.toNumberString()} $unit"
        binding.ml200!!.text = "${SharedPreferencesManager.value_200.toNumberString()} $unit"
        binding.ml250!!.text = "${SharedPreferencesManager.value_250.toNumberString()} $unit"
        binding.ml300!!.text = "${SharedPreferencesManager.value_300.toNumberString()} $unit"
        binding.ml350!!.text = "${SharedPreferencesManager.value_350.toNumberString()} $unit"
    }

    private fun initBottomBar() {

        menuNotify = binding.bottomBarNotify.menu
        menuNotNotify = binding.bottomBarNotNotify.menu

        createMenu()

    }

    private fun createMenu() {
        if(SharedPreferencesManager.themeInt > 1){
            SharedPreferencesManager.themeInt = 1
        }

        var themeInt = SharedPreferencesManager.themeInt


        var colorString = when (themeInt) {
            0 -> {
                "#41B279"
            }
            1 -> {
                "#29704D"
            }
            else -> "#41B279"
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
            R.id.icon_info -> goToBottomInfo()
            R.id.icon_trophy -> goToThrophyList()
            R.id.icon_stats -> goToStatsActivity()
        }
    }

    private fun goToStatsActivity() {
        Handler(requireContext().mainLooper).postDelayed({
            SharedPreferencesManager.totalIntake = totalIntake
            SharedPreferencesManager.unitString = unit
            safeNavController?.safeNavigate(DrinkFragmentDirections
                .actionDrinkFragmentToStatsFragment())
        }, TIME)

    }

    private fun goToBottomInfo() {
        Handler(requireContext().mainLooper).postDelayed({

            SharedPreferencesManager.totalIntake = totalIntake
            safeNavController?.safeNavigate(
                DrinkFragmentDirections.actionDrinkFragmentToInfoFragment())

        }, TIME)

    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun addDrinkedWater() {

        if (selectedOption != null) {
            if ((inTook * 100 / totalIntake) <= 130 && clicked <= 1) {
                if (sqliteHelper.addIntook(dateNow, selectedOption!!,unit, dateNow.toMonth(),
                    dateNow.toYear()) > 0) {
                    sqliteHelper.updateTotalIntake(dateNow,totalIntake,unit)
                    inTook += selectedOption!!
                    setWaterLevel(inTook, totalIntake)
                    showMessage(
                        getString(R.string.your_water_intake_was_saved), viewWindow, false,
                        AppUtils.Companion.TypeMessage.SAVE
                    )
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
            addBackground(binding.op50ml,binding.op100ml,binding.op150ml,binding.op200ml,binding.op250ml,binding.op300ml,binding.opCustom,binding.opDrinkAll,binding.op350ml,outValue.resourceId,-1)

            // remove pending notifications
            val mNotificationManager : NotificationManager = requireActivity()
                .getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancelAll()
        }
    }

    private fun addBackground(
        op50ml: LinearLayout,
        op100ml: LinearLayout,
        op150ml: LinearLayout,
        op200ml: LinearLayout,
        op250ml: LinearLayout,
        op300ml: LinearLayout,
        opCustom: LinearLayout,
        opDrinkAll: LinearLayout,
        op350ml: LinearLayout,
        resourceId: Int,
        oneHighLight: Int,
        highlight: Int? = null
    ) {
        op50ml.background = requireContext().getDrawable((if(oneHighLight==1){highlight}else{resourceId})!!)
        op100ml.background = requireContext().getDrawable((if(oneHighLight==2){highlight}else{resourceId})!!)
        op150ml.background = requireContext().getDrawable((if(oneHighLight==3){highlight}else{resourceId})!!)
        op200ml.background = requireContext().getDrawable((if(oneHighLight==4){highlight}else{resourceId})!!)
        op250ml.background = requireContext().getDrawable((if(oneHighLight==5){highlight}else{resourceId})!!)
        op300ml.background = requireContext().getDrawable((if(oneHighLight==6){highlight}else{resourceId})!!)
        opCustom.background = requireContext().getDrawable((if(oneHighLight==7){highlight}else{resourceId})!!)
        opDrinkAll.background = requireContext().getDrawable((if(oneHighLight==8){highlight}else{resourceId})!!)
        op350ml.background = requireContext().getDrawable((if(oneHighLight==9){highlight}else{resourceId})!!)
    }


    private fun addLastIntook(toFloat: Float) {
        SharedPreferencesManager.lastIntook = toFloat
    }

    private fun goToThrophyList() {
        val li = LayoutInflater.from(requireContext())
        val promptsView = li.inflate(R.layout.custom_input_dialog3, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(promptsView)

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun manageNotification() {
        val alarm = AlarmHelper()
        if(enabled){
            notificStatus = !notificStatus
            SharedPreferencesManager.notificationStatus =  notificStatus
            if (notificStatus) {
                binding.bottomBarNotNotify.visibility = View.GONE
                binding.bottomBarNotify.visibility = View.VISIBLE
                Snackbar.make(viewWindow, getString(R.string.notification_enabled), Snackbar.LENGTH_SHORT).show()
                alarm.setAlarm(
                    requireContext(),
                    SharedPreferencesManager.notificationFreq.toLong())
            } else {
                binding.bottomBarNotNotify.visibility = View.VISIBLE
                binding.bottomBarNotify.visibility = View.GONE
                Snackbar.make(viewWindow, getString(R.string.notification_disabled), Snackbar.LENGTH_SHORT).show()
                alarm.cancelAlarm(requireContext())
            }
        }
    }

    private fun setBackGround() {
        var themeInt = SharedPreferencesManager.themeInt
        when(themeInt){
            0->toLightTheme()
            1->toDarkTheme()
        }
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
        binding.intakeProgress.colorBackground = requireContext().getColor(R.color.teal_700)
        binding.undoTV.setTextColor(resources.getColor(R.color.colorBlack))
        binding.redoTV.setTextColor(resources.getColor(R.color.colorBlack))
        binding.refreshTV.setTextColor(resources.getColor(R.color.colorBlack))
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
        binding.undoTV.setTextColor(resources.getColor(R.color.colorBlack))
        binding.redoTV.setTextColor(resources.getColor(R.color.colorBlack))
        binding.refreshTV.setTextColor(resources.getColor(R.color.colorBlack))
    }

    private fun setTheme() {
        val theme = SharedPreferencesManager.themeInt.toMainTheme()
        requireActivity().setTheme(theme)

    }

    private fun updateValues() {
        SharedPreferencesManager.noUpdateUnit = false
        SharedPreferencesManager.totalIntake = totalIntake

        inTook = sqliteHelper.getIntook(dateNow)

        if(totalIntake!=0f){
            sqliteHelper.addAll(dateNow, inTook, totalIntake,unit,
                dateNow.toMonth(), dateNow.toYear())
        }

        setWaterLevel(inTook, totalIntake)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()

        setBackGround()

        if(sqliteHelper.getAvisDay(dateNow)){
            binding.tvIntook.setTextColor(resources.getColor(R.color.red))
            binding.tvTotalIntake.setTextColor(resources.getColor(R.color.red))
        }

        val new = SharedPreferencesManager.resetNotification
        if(new){
            val mNotificationManager : NotificationManager = requireActivity()
                .getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotificationManager.deleteNotificationChannel("rpt.tools.mementobibere.CHANNELONE")
            }
            SharedPreferencesManager.resetNotification = false
        }

        if(!SharedPreferencesManager.setGender){
            setGender()
        }

        if(!SharedPreferencesManager.setBloodDonor){
            setBloodDonor()
        }

        if(!SharedPreferencesManager.setWorkOut){
            setNewWorkType()
        }

        if(!SharedPreferencesManager.setClimate){
            setClimate()
        }

        setValueForDrinking()

        outValue = TypedValue()
        requireContext().applicationContext.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )

        if(!SharedPreferencesManager.setWeight){
            safeNavController?.safeNavigate(DrinkFragmentDirections.actionDrinkFragmentToSelectWeightBottomSheetFragment())
        }

        if(!AppUtils.isValidDate(
                Date(SharedPreferencesManager.sleepingTime).toStringHour(),
                Date(SharedPreferencesManager.wakeUpTime).toStringHour())){
            safeNavController?.safeNavigate(DrinkFragmentDirections.actionDrinkFragmentToAdjustHourBottomSheetFragment())
        }

        notificStatus = SharedPreferencesManager.notificationStatus
        val alarm = AlarmHelper()
        if (!alarm.checkAlarm(requireContext()) && notificStatus) {
            binding.bottomBarNotNotify.visibility = GONE
            binding.bottomBarNotify.visibility = VISIBLE
            var isAvisDay = sqliteHelper.getAvisDay(dateNow)
            var freq = SharedPreferencesManager.notificationFreq.toLong()
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

        updateValues()

        var background = R.drawable.option_select_bg

        if(SharedPreferencesManager.setTips){
            if(inTook > 0){
                binding.bubble.visibility = VISIBLE
                binding.se.visibility = VISIBLE
                binding.top.visibility = VISIBLE
                val randomIndex: Int = java.util.Random().nextInt(waters.size)
                val randomWaters: String = waters[randomIndex]
                binding.se.text = randomWaters
            }
            else{
                binding.top.visibility = GONE
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
            addBackground(binding.op50ml,binding.op100ml,binding.op150ml,binding.op200ml,
                binding.op250ml,binding.op300ml,binding.opCustom,binding.opDrinkAll,
                binding.op350ml,outValue.resourceId,1,background)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.op100ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 1
            selectedOption = binding.ml100!!.text.toString().toExtractFloat()
            addBackground(binding.op50ml,binding.op100ml,binding.op150ml,binding.op200ml,
                binding.op250ml,binding.op300ml,binding.opCustom,binding.opDrinkAll,
                binding.op350ml,outValue.resourceId,2,background)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.op150ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 2
            selectedOption = binding.ml150!!.text.toString().toExtractFloat()
            addBackground(binding.op50ml,binding.op100ml,binding.op150ml,binding.op200ml,
                binding.op250ml,binding.op300ml,binding.opCustom,binding.opDrinkAll,
                binding.op350ml,outValue.resourceId,3,background)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.op200ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 3
            selectedOption = binding.ml200!!.text.toString().toExtractFloat()
            addBackground(binding.op50ml,binding.op100ml,binding.op150ml,binding.op200ml,
                binding.op250ml,binding.op300ml,binding.opCustom,binding.opDrinkAll,
                binding.op350ml,outValue.resourceId,4,background)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.op250ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 4
            selectedOption = binding.ml250!!.text.toString().toExtractFloat()
            addBackground(binding.op50ml,binding.op100ml,binding.op150ml,binding.op200ml,
                binding.op250ml,binding.op300ml,binding.opCustom,binding.opDrinkAll,
                binding.op350ml,outValue.resourceId,5,background)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.op300ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 5
            selectedOption = binding.ml300!!.text.toString().toExtractFloat()
            addBackground(binding.op50ml,binding.op100ml,binding.op150ml,binding.op200ml,
                binding.op250ml,binding.op300ml,binding.opCustom,binding.opDrinkAll,
                binding.op350ml,outValue.resourceId,6,background)
            addDrinkedWater()
            randomizeBalloon()
        }

        binding.op350ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            btnSelected = 6
            selectedOption = binding.ml350!!.text.toString().toExtractFloat()
            addBackground(binding.op50ml,binding.op100ml,binding.op150ml,binding.op200ml,
                binding.op250ml,binding.op300ml,binding.opCustom,binding.opDrinkAll,
                binding.op350ml,outValue.resourceId,7,background)
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
                    btnSelected = 7
                    addDrinkedWater()
                    randomizeBalloon()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            addBackground(binding.op50ml,binding.op100ml,binding.op150ml,binding.op200ml,
                binding.op250ml,binding.op300ml,binding.opCustom,binding.opDrinkAll,
                binding.op350ml,outValue.resourceId,8,background)
        }

        binding.opDrinkAll.setOnClickListener {
            if(clicked < 1 && inTook < totalIntake){
                if (snackbar != null) {
                    snackbar?.dismiss()
                }
                clicked += 1
                btnSelected = 7
                selectedOption = AppUtils.calculateOption(inTook,totalIntake)!!
                addBackground(binding.op50ml,binding.op100ml,binding.op150ml,binding.op200ml,
                    binding.op250ml,binding.op300ml,binding.opCustom,binding.opDrinkAll,
                    binding.op350ml,outValue.resourceId,8,background)
                addDrinkedWater()
                randomizeBalloon()
            }
            else{
                showMessage(getString(R.string.option_selectable_once_a_day), it)
            }
        }

        if (!SharedPreferencesManager.firstRun || totalIntake > 0) {
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
                            SharedPreferencesManager.totalIntake = 2000f
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
                getString(R.string.tomorrow_you_will_donate), it, duration = 3500
            )
        }

        if ((inTook * 100 / totalIntake) >= 130) {
            binding.intakeProgress.labelText = "${
                getString(
                    R.string.you_achieved_the_goal
                )}"
        }

        if(SharedPreferencesManager.startTutorial){
            safeNavController?.safeNavigate(DrinkFragmentDirections
                .actionDrinkFragmentToTutorialFragment())
        }
    }

    private fun randomizeBalloon() {
        if(SharedPreferencesManager.setTips){
            if(binding.bubble.visibility == INVISIBLE || binding.bubble.visibility == GONE){
                binding.top.visibility = VISIBLE
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
                showMessage(getString(R.string.you_selected_avis), it)
            }
            else{
                bloodDonorChoice = 0
                showMessage(getString(R.string.you_selected_no_avis), it)
            }
        }

        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            SharedPreferencesManager.setBloodDonor = true
            SharedPreferencesManager.bloodDonorKey = bloodDonorChoice
        }.setNegativeButton("Cancel") { _, _ ->
            SharedPreferencesManager.setBloodDonor = true
            SharedPreferencesManager.bloodDonorKey = bloodDonorChoice
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
            showMessage(
                getString(R.string.you_selected_man), it,
                type=AppUtils.Companion.TypeMessage.MAN
            )
        }

        userWomanBtn.setOnClickListener {
            genderChoice = 1
            showMessage(
                getString(R.string.you_selected_woman), it,
                type=AppUtils.Companion.TypeMessage.WOMAN
            )
        }

        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            when (genderChoice) {
                -1 -> showMessage(getString(R.string.gender_hint), promptsView, true)
                else -> {
                    SharedPreferencesManager.setGender = true
                    SharedPreferencesManager.gender = genderChoice
                }
            }
        }.setNegativeButton("Cancel") { _, _ ->
            showMessage(getString(R.string.gender_hint), promptsView, true)
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
            SharedPreferencesManager.workType = workType
            showMessage(
                getString(R.string.you_selected_calm), it,
                type=AppUtils.Companion.TypeMessage.WORKTYPE, workType = workType
            )
        }

        btnNormal.setOnClickListener{
            workType = 1
            SharedPreferencesManager.workType = workType
            showMessage(
                getString(R.string.you_selected_normal), it,
                type=AppUtils.Companion.TypeMessage.WORKTYPE, workType = workType
            )
        }

        btnLively.setOnClickListener{
            workType = 2
            SharedPreferencesManager.workType = workType
            showMessage(
                getString(R.string.you_selected_lively), it,
                type=AppUtils.Companion.TypeMessage.WORKTYPE, workType = workType
            )
        }

        btnIntense.setOnClickListener{
            workType = 3
            SharedPreferencesManager.workType = workType
            showMessage(
                getString(R.string.you_selected_intense), it,
                type=AppUtils.Companion.TypeMessage.WORKTYPE, workType = workType
            )
        }

        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            when (workType) {
                -1 -> showMessage(getString(R.string.work_type_hint), promptsView, true)
                else -> {

                    val totalIntake = AppUtils.calculateIntake(
                        SharedPreferencesManager.weight,
                        workType,
                        SharedPreferencesManager.weightUnit,
                        SharedPreferencesManager.gender,
                        SharedPreferencesManager.climate,0,
                        SharedPreferencesManager.current_unitInt
                    )
                    val df = DecimalFormat("#")
                    df.roundingMode = RoundingMode.CEILING
                    SharedPreferencesManager.totalIntake = df.format(totalIntake).toFloat()

                    sqliteHelper.updateTotalIntake(
                        AppUtils.getCurrentOnlyDate()!!,
                        df.format(totalIntake).toFloat(), AppUtils.calculateExtensions(
                            SharedPreferencesManager.new_unitInt)
                    )
                    SharedPreferencesManager.workType = workType
                    SharedPreferencesManager.setWorkOut = true
                    safeNavController?.safeNavigate(DrinkFragmentDirections
                        .actionDrinkFragmentToSelfFragment())
                }
            }
        }.setNegativeButton("Cancel") { _, _ ->
            showMessage(getString(R.string.work_type_hint), promptsView, true)
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
            SharedPreferencesManager.climate = climate
            showMessage(
                getString(R.string.you_selected_cold), it,
                type=AppUtils.Companion.TypeMessage.CLIMATE
            )
        }

        btnFresh.setOnClickListener{
            climate = 1
            SharedPreferencesManager.climate = climate
            showMessage(
                getString(R.string.you_selected_fresh), it,
                type=AppUtils.Companion.TypeMessage.CLIMATE
            )
        }

        btnMild.setOnClickListener{
            climate = 2
            SharedPreferencesManager.climate = climate
            showMessage(
                getString(R.string.you_selected_mild), it,
                type=AppUtils.Companion.TypeMessage.CLIMATE
            )
        }

        btnTorrid.setOnClickListener{
            climate = 3
            SharedPreferencesManager.climate = climate
            showMessage(
                getString(R.string.you_selected_torrid), it,
                type=AppUtils.Companion.TypeMessage.CLIMATE
            )
        }

        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            when (climate) {
                -1 -> showMessage(getString(R.string.climate_set_hint), promptsView, true)
                else -> {
                    val totalIntake = AppUtils.calculateIntake(
                        SharedPreferencesManager.weight,
                        SharedPreferencesManager.workType,
                        SharedPreferencesManager.weightUnit,
                        SharedPreferencesManager.gender,
                        climate,0,
                        SharedPreferencesManager.current_unitInt
                    )
                    val df = DecimalFormat("#")
                    df.roundingMode = RoundingMode.CEILING
                    SharedPreferencesManager.totalIntake = df.format(totalIntake).toFloat()

                    sqliteHelper.updateTotalIntake(
                        AppUtils.getCurrentOnlyDate()!!,
                        df.format(totalIntake).toFloat(), AppUtils.calculateExtensions(
                            SharedPreferencesManager.new_unitInt)
                    )
                    SharedPreferencesManager.climate = climate
                    SharedPreferencesManager.setClimate = true
                    safeNavController?.safeNavigate(DrinkFragmentDirections
                        .actionDrinkFragmentToSelfFragment())
                }
            }
        }.setNegativeButton("Cancel") { _, _ ->
            showMessage(getString(R.string.climate_set_hint), promptsView, true)
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
        if(btnSelected != null) {
            var totalIntook = sqliteHelper.getIntook(AppUtils.getCurrentDate()!!)
            if(selectedOption != null){
                sqliteHelper.resetIntook(AppUtils.getCurrentDate()!!)
                sqliteHelper.addIntook(AppUtils.getCurrentDate()!!,(totalIntook + selectedOption!!),unit,
                    AppUtils.getCurrentDate()!!.toMonth(), AppUtils.getCurrentDate()!!.toYear())
                sqliteHelper.addOrUpdateIntookCounter(dateNow,btnSelected!!.toFloat(), 1)
                updateValues()
                addLastIntook(AppUtils.convertToSelected(selectedOption!!,unit))
                selectedOption = null
                counter = 0
                refreshed = false
                intookToRefresh = 0f
                randomizeBalloon()
            }
        }
    }

    private fun undoLastDailyIntook() {
        var totalIntook = sqliteHelper.getIntook(dateNow)
        if(selectedOption != null && counter == 0) {
            sqliteHelper.resetIntook(dateNow)
            sqliteHelper.addIntook(
                AppUtils.getCurrentDate()!!,
                (totalIntook - selectedOption!!),
                unit,dateNow.toMonth(),dateNow.toYear()
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
            binding.top.visibility = GONE
            binding.bubble.visibility = GONE
            binding.se.visibility = GONE
        }
    }

    override fun onResume() {
        super.onResume()
        setTheme()
        setBackGround()
        refreshAlarm(SharedPreferencesManager.notificationStatus)
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


        if ((inTook * 100 / totalIntake) >= 130) {
            showMessage(getString(R.string.you_achieved_the_goal), binding.mainActivityParent)
            sqliteHelper.addReachedGoal(dateNow,inTook,unit)
        }
    }

    private fun setValueForDrinking() {
        unit = AppUtils.calculateExtensions(SharedPreferencesManager.new_unitInt)
        binding.ml50!!.text = "${SharedPreferencesManager.value_50.toNumberString()} $unit"
        binding.ml100!!.text = "${SharedPreferencesManager.value_100.toNumberString()} $unit"
        binding.ml150!!.text = "${SharedPreferencesManager.value_150.toNumberString()} $unit"
        binding.ml200!!.text = "${SharedPreferencesManager.value_200.toNumberString()} $unit"
        binding.ml250!!.text = "${SharedPreferencesManager.value_250.toNumberString()} $unit"
        binding.ml300!!.text = "${SharedPreferencesManager.value_300.toNumberString()} $unit"
        binding.ml350!!.text = "${SharedPreferencesManager.value_350.toNumberString()} $unit"
        binding.tvTotalIntake!!.text = "/" +totalIntake.toNumberString() + " " + "$unit"
    }

    companion object {
        const val TIME: Long = 150
    }

    @Deprecated("Deprecated in Java")
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
                    binding.bottomBarNotNotify.visibility = GONE
                    binding.bottomBarNotify.visibility = VISIBLE
                    Snackbar.make(viewWindow, getString(R.string.notification_enabled), Snackbar.LENGTH_SHORT).show()
                    alarm.setAlarm(
                        requireContext(),
                        SharedPreferencesManager.notificationFreq.toLong())

                } else {
                    binding.bottomBarNotNotify.visibility = VISIBLE
                    binding.bottomBarNotify.visibility = GONE
                    Snackbar.make(viewWindow, getString(R.string.notification_disabled), Snackbar.LENGTH_SHORT).show()
                    alarm.cancelAlarm(requireContext())
                }
            }
        }
    }

    private fun refreshAlarm(notify: Boolean){
        var alarm = AlarmHelper()
        if(notify){
            binding.bottomBarNotNotify.visibility = GONE
            binding.bottomBarNotify.visibility = VISIBLE
            alarm.setAlarm(
                requireContext(),
                SharedPreferencesManager.notificationFreq.toLong())
        }
        else{
            binding.bottomBarNotNotify.visibility = VISIBLE
            binding.bottomBarNotify.visibility = GONE
            alarm.cancelAlarm(requireContext())
            val activity: Activity? = activity
            if (activity != null && activity is MainActivity) {
                activity.initPermissions()
            }
        }
    }
}