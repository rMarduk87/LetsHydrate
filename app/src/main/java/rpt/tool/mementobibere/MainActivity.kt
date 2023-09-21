package rpt.tool.mementobibere


import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import rpt.tool.mementobibere.databinding.ActivityMainBinding
import rpt.tool.mementobibere.ui.libraries.menu.Menu
import rpt.tool.mementobibere.ui.libraries.menu.MenuItem
import rpt.tool.mementobibere.ui.libraries.menu.MenuItemDescriptor
import rpt.tool.mementobibere.ui.userinfo.EditInfoBottomSheetFragment
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.AppUtils.Companion.NO_UPDATE_UNIT
import rpt.tool.mementobibere.utils.AppUtils.Companion.extractIntConversion
import rpt.tool.mementobibere.utils.AppUtils.Companion.intentRequestCode
import rpt.tool.mementobibere.utils.AppUtils.Companion.listIconNotNotify
import rpt.tool.mementobibere.utils.AppUtils.Companion.listIconNotify
import rpt.tool.mementobibere.utils.AppUtils.Companion.listIds
import rpt.tool.mementobibere.utils.AppUtils.Companion.listStringNotNotify
import rpt.tool.mementobibere.utils.AppUtils.Companion.listStringNotify
import rpt.tool.mementobibere.utils.extensions.toCalculatedValue
import rpt.tool.mementobibere.utils.extensions.toExtractFloat
import rpt.tool.mementobibere.utils.extensions.toMainTheme
import rpt.tool.mementobibere.utils.extensions.toNumberString
import rpt.tool.mementobibere.utils.helpers.AlarmHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.log.w
import rpt.tool.mementobibere.utils.permissions.PermissionManager
import rpt.tool.mementobibere.utils.permissions.dispatcher.dsl.checkPermissions
import rpt.tool.mementobibere.utils.permissions.dispatcher.dsl.doOnDenied
import rpt.tool.mementobibere.utils.permissions.dispatcher.dsl.doOnGranted
import rpt.tool.mementobibere.utils.permissions.dispatcher.dsl.withRequestCode
import java.util.Calendar
import java.util.Date


class MainActivity : RPTBaseAppCompatActivity() {

    private var enabled: Boolean = true
    private var stopSleepTime: Long = 0
    private var startSleepTime: Long = 0
    private lateinit var unit: String
    private lateinit var menuNotify: Menu
    private lateinit var menuNotNotify: Menu
    private lateinit var outValue: TypedValue
    private lateinit var view: View
    private lateinit var alarm: AlarmHelper
    private var totalIntake: Float = 0f
    private var inTook: Float = 0f
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var dateNow: String
    private var notificStatus: Boolean = false
    private var selectedOption: Float? = null
    private var snackbar: Snackbar? = null
    private var doubleBackToExitPressedOnce = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener
    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private var themeInt: Int = 0
    private var current_unitInt: Int = 0
    private var new_unitInt: Int = 0
    private var value_50: Float = 50f
    private var value_100: Float = 100f
    private var value_150: Float = 150f
    private var value_200: Float = 200f
    private var value_250: Float = 250f

    private val pm = PermissionManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        current_unitInt = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        new_unitInt = sharedPref.getInt(AppUtils.UNIT_NEW_KEY,0)
        value_50 = sharedPref.getFloat(AppUtils.VALUE_50_KEY,50f)
        value_100 = sharedPref.getFloat(AppUtils.VALUE_100_KEY,100f)
        value_150 = sharedPref.getFloat(AppUtils.VALUE_150_KEY,150f)
        value_200 = sharedPref.getFloat(AppUtils.VALUE_200_KEY,200f)
        value_250 = sharedPref.getFloat(AppUtils.VALUE_250_KEY,250f)
        startSleepTime = sharedPref.getLong(AppUtils.START_TIME_KEY, 1558323000000)
        stopSleepTime = sharedPref.getLong(AppUtils.STOP_TIME_KEY,  1558323000000)
        setTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBackGround()
        initPermissions()
        initInAppUpdate()


        sqliteHelper = SqliteHelper(this)

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
            startActivity(Intent(this, WalkThroughActivity::class.java))
            finish()
        } else if (totalIntake <= 0) {
            startActivity(Intent(this, InitUserInfoActivity::class.java))
            finish()
        }

        dateNow = AppUtils.getCurrentOnlyDate()!!

        view = window.decorView.findViewById<View>(android.R.id.content)
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
    }

    private fun initBottomBar() {

        menuNotify = binding.bottomBarNotify.menu
        menuNotNotify = binding.bottomBarNotNotify.menu

        createMenu()

    }

    private fun createMenu() {

        for (i in listIds.indices) {
            menuNotify.add(
                MenuItemDescriptor.Builder(
                    this,
                    listIds[i],
                    listIconNotify[i],
                    listStringNotify[i],
                    Color.parseColor("#41B279")
                )
                    .build()
            )
        }

        for (i in listIds.indices) {
            menuNotNotify.add(
                MenuItemDescriptor.Builder(
                    this,
                    listIds[i],
                    listIconNotNotify[i],
                    listStringNotNotify[i],
                    Color.parseColor("#41B279")
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
            R.id.icon_plus -> addDrinkedWater()
            R.id.icon_other -> goToBottomInfo()
            R.id.icon_stats -> goToStatsActivity()
        }
    }

    private fun goToStatsActivity() {
        Handler(this.mainLooper).postDelayed({
            var edit = sharedPref.edit()
            edit.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntake)
            edit.putString(AppUtils.UNIT_STRING,unit)
            edit.apply()
            startActivity(Intent(this, StatsActivity::class.java))

        }, TIME)

    }

    private fun goToBottomInfo() {
        Handler(this.mainLooper).postDelayed({

            var edit = sharedPref.edit()
            edit.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntake)
            edit.apply()
            startActivity(Intent(this, InfoActivity::class.java))

        }, TIME)

    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun addDrinkedWater() {

        if (selectedOption != null) {
            if ((inTook * 100 / totalIntake) <= 140) {
                if (sqliteHelper.addIntook(dateNow, selectedOption!!,unit) > 0) {
                    inTook += selectedOption!!
                    setWaterLevel(inTook, totalIntake)
                    showMessage(getString(R.string.your_water_intake_was_saved),view,false,
                        AppUtils.Companion.TypeMessage.SAVE)
                }
            } else {
                showMessage(getString(R.string.you_already_achieved_the_goal), view)
            }
            selectedOption = null
            binding.tvCustom.text = "Custom"
            binding.op50ml.background = getDrawable(outValue.resourceId)
            binding.op100ml.background = getDrawable(outValue.resourceId)
            binding.op150ml.background = getDrawable(outValue.resourceId)
            binding.op200ml.background = getDrawable(outValue.resourceId)
            binding.op250ml.background = getDrawable(outValue.resourceId)
            binding.opCustom.background = getDrawable(outValue.resourceId)

            // remove pending notifications
            val mNotificationManager : NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancelAll()
        } else {
            YoYo.with(Techniques.Shake)
                .duration(700)
                .playOn(binding.cardView)
            showMessage(getString(R.string.please_select_an_option),view,true)
        }
        binding.bottomBarNotify.menu.findItemById(R.id.icon_plus).notification().clear()
        binding.bottomBarNotNotify.menu.findItemById(R.id.icon_plus).notification().clear()
    }

    private fun goToBottomEdit() {
        var editor = sharedPref.edit()
        editor.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntake)
        editor.apply()
        Handler(this.mainLooper).postDelayed({

            val bottomSheetFragment = EditInfoBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)

        }, TIME)
    }

    private fun manageNotification() {
        if(enabled){
            notificStatus = !notificStatus
            var editor = sharedPref.edit()
            editor.putBoolean(AppUtils.NOTIFICATION_STATUS_KEY, notificStatus)
            editor.apply()
            if (notificStatus) {
                binding.bottomBarNotNotify.visibility = GONE
                binding.bottomBarNotify.visibility = VISIBLE
                Snackbar.make(view, getString(R.string.notification_enabled), Snackbar.LENGTH_SHORT).show()
                alarm.setAlarm(
                    this,
                    sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
                )
            } else {
                binding.bottomBarNotNotify.visibility = VISIBLE
                binding.bottomBarNotify.visibility = GONE
                Snackbar.make(view, getString(R.string.notification_disabled), Snackbar.LENGTH_SHORT).show()
                alarm.cancelAlarm(this)
            }
        }
    }

    private fun setBackGround() {
        when(themeInt){
            0->binding.mainActivityParent.background = getDrawable(R.drawable.ic_app_bg)
            1->binding.mainActivityParent.background = getDrawable(R.drawable.ic_app_bg_dark)
        }
    }

    private fun setTheme() {
        val theme = themeInt.toMainTheme()
        setTheme(theme)

    }

    fun updateValues() {
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

        var noUpdate = sharedPref.getBoolean(NO_UPDATE_UNIT,false)
        if(!noUpdate){
            totalIntake = sharedPref.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
                .toCalculatedValue(current_unitInt,calculateRealUnit(current_unitInt,new_unitInt))

            unit = AppUtils.calculateExtensions(new_unitInt)
            sqliteHelper.updateTotalIntake(
                AppUtils.getCurrentOnlyDate()!!,
                totalIntake, unit)
        }

        var editor = sharedPref.edit()
        editor.putBoolean(NO_UPDATE_UNIT, false)
        editor.putFloat(AppUtils.TOTAL_INTAKE_KEY,totalIntake)
        editor.apply()

        sqliteHelper.addAll(dateNow, 0, totalIntake,unit)

        inTook = sqliteHelper.getIntook(dateNow)

        setWaterLevel(inTook, totalIntake)

        binding.bottomBarNotify.menu.findItemById(R.id.icon_plus).notification().clear()
        binding.bottomBarNotNotify.menu.findItemById(R.id.icon_plus).notification().clear()
    }

    private fun calculateRealUnit(currentUnitint: Int, newUnitint: Int): Int {
        val realUnit = new_unitInt
        if(currentUnitint==newUnitint){
            val c = sqliteHelper.getTotalIntake(dateNow)
            if(c.moveToFirst()){
                val unit = extractIntConversion(c.getString(1))
                return if(unit==new_unitInt){
                    realUnit
                } else{
                    unit
                }
            }
        }
        return realUnit
    }

    override fun onStart() {
        super.onStart()

        outValue = TypedValue()
        applicationContext.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )

        startSleepTime = sharedPref.getLong(AppUtils.START_TIME_KEY, 1558323000000)
        stopSleepTime = sharedPref.getLong(AppUtils.STOP_TIME_KEY,  1558323000000)

        val dataStart = Calendar.getInstance()
        dataStart.timeInMillis = startSleepTime
        val dataStop = Calendar.getInstance()
        dataStop.timeInMillis = stopSleepTime

        val builderFrom = StringBuilder()
        builderFrom.append(AppUtils.getCurrentOnlyDate()!!)
        builderFrom.append("-")
        builderFrom.append(String.format(
            "%02d:%02d",
            dataStart.get(Calendar.HOUR_OF_DAY),
            dataStart.get(Calendar.MINUTE)))

        val dateFrom = builderFrom.toString()

        val builderTo = StringBuilder()
        builderTo.append(AppUtils.getCurrentDatePlusOne()!!)
        builderTo.append("-")
        builderTo.append(String.format(
            "%02d:%02d",
            dataStop.get(Calendar.HOUR_OF_DAY),
            dataStop.get(Calendar.MINUTE)))

        val dateTo = builderTo.toString()

        notificStatus = sharedPref.getBoolean(AppUtils.NOTIFICATION_STATUS_KEY, true)
        alarm = AlarmHelper()
        if (!alarm.checkAlarm(this) && notificStatus) {
            binding.bottomBarNotNotify.visibility = GONE
            binding.bottomBarNotify.visibility = VISIBLE
            alarm.setAlarm(
                this,
                sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
            )
        }

        if(sleepMode(dateFrom,dateTo)){
            binding.bottomBarNotNotify.visibility = VISIBLE
            binding.bottomBarNotify.visibility = GONE
            showMessage(getString(R.string.notification_sleep),view,false,
                AppUtils.Companion.TypeMessage.SLEEP)
            alarm.cancelAlarm(this)
            enabled = false
        }
        else{
            if (!alarm.checkAlarm(this) && notificStatus) {
                binding.bottomBarNotNotify.visibility = GONE
                binding.bottomBarNotify.visibility = VISIBLE
                alarm.setAlarm(
                    this,
                    sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
                )
            }
        }



        current_unitInt = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        new_unitInt = sharedPref.getInt(AppUtils.UNIT_NEW_KEY,0)

        updateValues()

        binding.op50ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = binding.ml50!!.text.toString().toExtractFloat()
            binding.op50ml.background = getDrawable(R.drawable.option_select_bg)
            binding.op100ml.background = getDrawable(outValue.resourceId)
            binding.op150ml.background = getDrawable(outValue.resourceId)
            binding.op200ml.background = getDrawable(outValue.resourceId)
            binding.op250ml.background = getDrawable(outValue.resourceId)
            binding.opCustom.background = getDrawable(outValue.resourceId)
            val notification = binding.bottomBarNotify.menu.findItemById(R.id.icon_plus).notification()
            notification.show(selectedOption.toString())
            val notificationNot = binding.bottomBarNotNotify.menu.findItemById(R.id.icon_plus).notification()
            notificationNot.show(selectedOption.toString())
        }

        binding.op100ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = binding.ml100!!.text.toString().toExtractFloat()
            binding.op50ml.background = getDrawable(outValue.resourceId)
            binding.op100ml.background = getDrawable(R.drawable.option_select_bg)
            binding.op150ml.background = getDrawable(outValue.resourceId)
            binding.op200ml.background = getDrawable(outValue.resourceId)
            binding.op250ml.background = getDrawable(outValue.resourceId)
            binding.opCustom.background = getDrawable(outValue.resourceId)
            setNotificationBadge()

        }

        binding.op150ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = binding.ml150!!.text.toString().toExtractFloat()
            binding.op50ml.background = getDrawable(outValue.resourceId)
            binding.op100ml.background = getDrawable(outValue.resourceId)
            binding.op150ml.background = getDrawable(R.drawable.option_select_bg)
            binding.op200ml.background = getDrawable(outValue.resourceId)
            binding.op250ml.background = getDrawable(outValue.resourceId)
            binding.opCustom.background = getDrawable(outValue.resourceId)
            setNotificationBadge()

        }

        binding.op200ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = binding.ml200!!.text.toString().toExtractFloat()
            binding.op50ml.background = getDrawable(outValue.resourceId)
            binding.op100ml.background = getDrawable(outValue.resourceId)
            binding.op150ml.background = getDrawable(outValue.resourceId)
            binding.op200ml.background = getDrawable(R.drawable.option_select_bg)
            binding.op250ml.background = getDrawable(outValue.resourceId)
            binding.opCustom.background = getDrawable(outValue.resourceId)
            setNotificationBadge()

        }

        binding.op250ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = binding.ml250!!.text.toString().toExtractFloat()
            binding.op50ml.background = getDrawable(outValue.resourceId)
            binding.op100ml.background = getDrawable(outValue.resourceId)
            binding.op150ml.background = getDrawable(outValue.resourceId)
            binding.op200ml.background = getDrawable(outValue.resourceId)
            binding.op250ml.background = getDrawable(R.drawable.option_select_bg)
            binding.opCustom.background = getDrawable(outValue.resourceId)
            setNotificationBadge()
        }

        binding.opCustom.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }

            val li = LayoutInflater.from(this)
            val promptsView = li.inflate(R.layout.custom_input_dialog, null)

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(promptsView)

            val userInput = promptsView
                .findViewById(R.id.etCustomInput) as TextInputLayout

            alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                val inputText = userInput.editText!!.text.toString()
                if (!TextUtils.isEmpty(inputText)) {
                    binding.tvCustom.text = "$inputText $unit"
                    selectedOption = binding.tvCustom.text.toString().toExtractFloat()
                    setNotificationBadge()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            binding.op50ml.background = getDrawable(outValue.resourceId)
            binding.op100ml.background = getDrawable(outValue.resourceId)
            binding.op150ml.background = getDrawable(outValue.resourceId)
            binding.op200ml.background = getDrawable(outValue.resourceId)
            binding.op250ml.background = getDrawable(outValue.resourceId)
            binding.opCustom.background = getDrawable(R.drawable.option_select_bg)
        }
        if (!sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, true) || totalIntake > 0) {
            setValueForDrinking()
        }


    }

    private fun sleepMode(dateFrom: String, dateTo: String): Boolean {
        val dateCheck = AppUtils.getCurrentDate()!!
        val d1 = dateFrom.split("-")
        val d2 = dateTo.split("-")
        val c = dateCheck.split("-")


        val from = Date(d1[2].toInt(),(d1[1].toInt() - 1), d1[0].toInt(), (d1[3].split(":"))[0].toInt(), (d1[3].split(":"))[1].toInt())
        val to = Date(d2[2].toInt(),(d2[1].toInt() - 1), d2[0].toInt(), (d2[3].split(":"))[0].toInt(), (d2[3].split(":"))[1].toInt())
        val check = Date(c[2].toInt(),(c[1].toInt() - 1), c[0].toInt(), (c[3].split(":"))[0].toInt(), (c[3].split(":"))[1].toInt())

        return check > from && check < to
    }

    private fun setNotificationBadge() {
        val notification = binding.bottomBarNotify.menu.findItemById(R.id.icon_plus).notification()
        notification.show(selectedOption!!.toNumberString())
        val notificationNot = binding.bottomBarNotNotify.menu.findItemById(R.id.icon_plus).notification()
        notificationNot.show(selectedOption!!.toNumberString())
    }

    private fun invite() {
        val contentUri = Uri.parse("android.resource://$packageName/drawable/ic_launcher")

        val msg = StringBuilder()
        msg.append(getString(R.string.check_out_the_app_at))
        msg.append("\n")
        msg.append("https://play.google.com/store/apps/details?id=$packageName") //example :com.package.name


        if (contentUri != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
            shareIntent.type = "*/*"
            shareIntent.putExtra(Intent.EXTRA_TEXT, msg.toString())
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            try {
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(applicationContext,
                    getString(R.string.no_app_available), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        IMMEDIATE,
                        this,
                        intentRequestCode
                    )
                }
            }
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        current_unitInt = sharedPref.getInt(AppUtils.UNIT_KEY,0)
        new_unitInt = sharedPref.getInt(AppUtils.UNIT_NEW_KEY,0)
        setTheme()
        setBackGround()
        binding.bottomBarNotify.menu.findItemById(R.id.icon_plus).notification().clear()
        binding.bottomBarNotNotify.menu.findItemById(R.id.icon_plus).notification().clear()
        val editor = sharedPref.edit()
        editor.putInt(AppUtils.UNIT_KEY,new_unitInt)
        editor.apply()
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
        binding.intakeProgress.currentProgress = progress
        if ((inTook * 100 / totalIntake) > 140) {
            showMessage(getString(R.string.you_achieved_the_goal),binding.mainActivityParent)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Snackbar.make(
            this.window.decorView.findViewById(android.R.id.content),
            getString(R.string.please_click_back_again_to_exit),
            Snackbar.LENGTH_SHORT
        ).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1000)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        pm.dispatchOnRequestPermissionsResult(requestCode, grantResults)
    }

    private fun initInAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(baseContext)
        registerInAppUpdateResultWatcher()
        registerInstallStateResultWatcher()
        checkUpdates()
    }

    private fun registerInstallStateResultWatcher() {
        installStateUpdatedListener = InstallStateUpdatedListener { installState ->
            when (installState.installStatus()) {
                InstallStatus.DOWNLOADED -> appUpdateManager.completeUpdate()
                InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(
                    installStateUpdatedListener
                )

                else -> {}
            }
        }
        appUpdateManager.registerListener(installStateUpdatedListener)

    }

    private fun registerInAppUpdateResultWatcher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode != RESULT_OK) {
                    w("Update flow failed! Result code: " + it.resultCode)
                }
            }
    }

    private fun checkUpdates() {
        appUpdateManager = AppUpdateManagerFactory.create(baseContext)
        installStateUpdatedListener = InstallStateUpdatedListener { installState ->
            when (installState.installStatus()) {
                InstallStatus.DOWNLOADED ->  appUpdateManager.completeUpdate()
                InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(
                    installStateUpdatedListener
                )
                else -> {}
            }
        }
        appUpdateManager.registerListener(installStateUpdatedListener)


        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
            ) {

                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    IMMEDIATE,
                    this,
                    intentRequestCode
                )
            }
        }
    }

    private fun initPermissions() {
        pm.buildRequestResultsDispatcher {
            withRequestCode(1) {
                checkPermissions(android.Manifest.permission.POST_NOTIFICATIONS)
                doOnGranted {
                    d("Post notification permission granted")
                }
                doOnDenied {
                    d("Post notification permission denied")
                }
            }
        }

        pm checkRequestAndDispatch 1
    }


    private fun setValueForDrinking() {
        unit = AppUtils.calculateExtensions(new_unitInt)
        value_50 = sharedPref.getFloat(AppUtils.VALUE_50_KEY,50f)
        value_100 = sharedPref.getFloat(AppUtils.VALUE_100_KEY,100f)
        value_150 = sharedPref.getFloat(AppUtils.VALUE_150_KEY,150f)
        value_200 = sharedPref.getFloat(AppUtils.VALUE_200_KEY,200f)
        value_250 = sharedPref.getFloat(AppUtils.VALUE_250_KEY,250f)
        binding.ml50!!.text = "${value_50.toNumberString()} $unit"
        binding.ml100!!.text = "${value_100.toNumberString()} $unit"
        binding.ml150!!.text = "${value_150.toNumberString()} $unit"
        binding.ml200!!.text = "${value_200.toNumberString()} $unit"
        binding.ml250!!.text = "${value_250.toNumberString()} $unit"
        binding.tvTotalIntake!!.text = "/" +totalIntake.toNumberString() + " " + "$unit"
    }

    companion object {

        const val TIME: Long = 1000
    }

}
