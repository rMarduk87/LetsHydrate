package rpt.tool.mementobibere

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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
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
import rpt.tool.mementobibere.ui.fragments.info.InfoBottomSheetFragment
import rpt.tool.mementobibere.ui.fragments.settings.SettingsBottomSheetFragment
import rpt.tool.mementobibere.ui.fragments.userinfo.EditInfoBottomSheetFragment
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.AppUtils.Companion.intentRequestCode
import rpt.tool.mementobibere.utils.helpers.AlarmHelper
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import rpt.tool.mementobibere.utils.log.d
import rpt.tool.mementobibere.utils.log.w
import rpt.tool.mementobibere.utils.permissions.PermissionManager
import rpt.tool.mementobibere.utils.permissions.dispatcher.dsl.checkPermissions
import rpt.tool.mementobibere.utils.permissions.dispatcher.dsl.doOnDenied
import rpt.tool.mementobibere.utils.permissions.dispatcher.dsl.doOnGranted
import rpt.tool.mementobibere.utils.permissions.dispatcher.dsl.withRequestCode


class MainActivity : AppCompatActivity() {

    private var totalIntake: Int = 0
    private var inTook: Int = 0
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var dateNow: String
    private var notificStatus: Boolean = false
    private var selectedOption: Int? = null
    private var snackbar: Snackbar? = null
    private var doubleBackToExitPressedOnce = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener
    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private val pm = PermissionManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPermissions()
        initInAppUpdate()

        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        sqliteHelper = SqliteHelper(this)

        totalIntake = sharedPref.getInt(AppUtils.TOTAL_INTAKE, 0)

        if (sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, true)) {
            startActivity(Intent(this, WalkThroughActivity::class.java))
            finish()
        } else if (totalIntake <= 0) {
            startActivity(Intent(this, InitUserInfoActivity::class.java))
            finish()
        }

        dateNow = AppUtils.getCurrentDate()!!

    }

    fun updateValues() {
        totalIntake = sharedPref.getInt(AppUtils.TOTAL_INTAKE, 0)

        inTook = sqliteHelper.getIntook(dateNow)

        setWaterLevel(inTook, totalIntake)
    }

    override fun onStart() {
        super.onStart()

        val outValue = TypedValue()
        applicationContext.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )

        notificStatus = sharedPref.getBoolean(AppUtils.NOTIFICATION_STATUS_KEY, true)
        val alarm = AlarmHelper()
        if (!alarm.checkAlarm(this) && notificStatus) {
            binding.btnNotific.setImageDrawable(getDrawable(R.drawable.ic_bell))
            alarm.setAlarm(
                this,
                sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
            )
        }

        if (notificStatus) {
            binding.btnNotific.setImageDrawable(getDrawable(R.drawable.ic_bell))
        } else {
            binding.btnNotific.setImageDrawable(getDrawable(R.drawable.ic_bell_disabled))
        }

        sqliteHelper.addAll(dateNow, 0, totalIntake)

        updateValues()

        binding.btnMenu.setOnClickListener {
            val bottomSheetFragment = EditInfoBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        binding.btnSettings.setOnClickListener {
            val bottomSheetFragment = SettingsBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        binding.btnInfo.setOnClickListener {
            val bottomSheetFragment = InfoBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        binding.btnMenu2.setOnClickListener {
            invite()
        }

        binding.fabAdd.setOnClickListener {
            if (selectedOption != null) {
                if ((inTook * 100 / totalIntake) <= 140) {
                    if (sqliteHelper.addIntook(dateNow, selectedOption!!) > 0) {
                        inTook += selectedOption!!
                        setWaterLevel(inTook, totalIntake)
                        showMessage(getString(R.string.your_water_intake_was_saved),it,false,1)
                    }
                } else {
                    showMessage(getString(R.string.you_already_achieved_the_goal), it)
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
                showMessage(getString(R.string.please_select_an_option),it,true)
            }
        }

        binding.btnNotific.setOnClickListener {
            notificStatus = !notificStatus
            sharedPref.edit().putBoolean(AppUtils.NOTIFICATION_STATUS_KEY, notificStatus).apply()
            if (notificStatus) {
                binding.btnNotific.setImageDrawable(getDrawable(R.drawable.ic_bell))
                Snackbar.make(it, getString(R.string.notification_enabled), Snackbar.LENGTH_SHORT).show()
                alarm.setAlarm(
                    this,
                    sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
                )
            } else {
                binding.btnNotific.setImageDrawable(getDrawable(R.drawable.ic_bell_disabled))
                Snackbar.make(it, getString(R.string.notification_disabled), Snackbar.LENGTH_SHORT).show()
                alarm.cancelAlarm(this)
            }
        }

        binding.btnStats.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }


        binding.op50ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 50
            binding.op50ml.background = getDrawable(R.drawable.option_select_bg)
            binding.op100ml.background = getDrawable(outValue.resourceId)
            binding.op150ml.background = getDrawable(outValue.resourceId)
            binding.op200ml.background = getDrawable(outValue.resourceId)
            binding.op250ml.background = getDrawable(outValue.resourceId)
            binding.opCustom.background = getDrawable(outValue.resourceId)

        }

        binding.op100ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 100
            binding.op50ml.background = getDrawable(outValue.resourceId)
            binding.op100ml.background = getDrawable(R.drawable.option_select_bg)
            binding.op150ml.background = getDrawable(outValue.resourceId)
            binding.op200ml.background = getDrawable(outValue.resourceId)
            binding.op250ml.background = getDrawable(outValue.resourceId)
            binding.opCustom.background = getDrawable(outValue.resourceId)

        }

        binding.op150ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 150
            binding.op50ml.background = getDrawable(outValue.resourceId)
            binding.op100ml.background = getDrawable(outValue.resourceId)
            binding.op150ml.background = getDrawable(R.drawable.option_select_bg)
            binding.op200ml.background = getDrawable(outValue.resourceId)
            binding.op250ml.background = getDrawable(outValue.resourceId)
            binding.opCustom.background = getDrawable(outValue.resourceId)

        }

        binding.op200ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 200
            binding.op50ml.background = getDrawable(outValue.resourceId)
            binding.op100ml.background = getDrawable(outValue.resourceId)
            binding.op150ml.background = getDrawable(outValue.resourceId)
            binding.op200ml.background = getDrawable(R.drawable.option_select_bg)
            binding.op250ml.background = getDrawable(outValue.resourceId)
            binding.opCustom.background = getDrawable(outValue.resourceId)

        }

        binding.op250ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 250
            binding.op50ml.background = getDrawable(outValue.resourceId)
            binding.op100ml.background = getDrawable(outValue.resourceId)
            binding.op150ml.background = getDrawable(outValue.resourceId)
            binding.op200ml.background = getDrawable(outValue.resourceId)
            binding.op250ml.background = getDrawable(R.drawable.option_select_bg)
            binding.opCustom.background = getDrawable(outValue.resourceId)

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
                    binding.tvCustom.text = "$inputText ml"
                    selectedOption = inputText.toInt()
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
    }


    private fun setWaterLevel(inTook: Int, totalIntake: Int) {

        YoYo.with(Techniques.SlideInDown)
            .duration(500)
            .playOn(binding.tvIntook)
        binding.tvIntook.text = "$inTook"
        binding.tvTotalIntake.text = "/$totalIntake ml"
        val progress = ((inTook / totalIntake.toFloat()) * 100).toInt()
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

    private fun showMessage(message: String, view: View, error:Boolean? = false, type:Int?=null) {
        val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        val customSnackView: View =
            when(error){
                true ->layoutInflater.inflate(R.layout.error_toast_layout, null)
                else->layoutInflater.inflate(R.layout.info_toast_layout, null)
            }
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackbarLayout = snackBar.view as Snackbar.SnackbarLayout

        val text = customSnackView.findViewById<TextView>(R.id.tvMessage)
        text.text = message

        if(type!=null){
            val anim = customSnackView.findViewById<LottieAnimationView>(R.id.anim)
            anim.setAnimation(R.raw.save)
        }

        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(customSnackView, 0)
        snackBar.show()
    }

}