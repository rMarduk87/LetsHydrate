package rpt.tool.mementobibere

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import rpt.tool.mementobibere.databinding.ActivityInfoBinding
import rpt.tool.mementobibere.databinding.PartialInfoBinding
import rpt.tool.mementobibere.databinding.PartialPrincipalInfoBinding
import rpt.tool.mementobibere.ui.libraries.alert.dialog.SweetAlertDialog
import rpt.tool.mementobibere.ui.libraries.menu.MenuItemDescriptor
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.AppUtils.Companion.UNIT_KEY
import rpt.tool.mementobibere.utils.AppUtils.Companion.calculateExtensions
import rpt.tool.mementobibere.utils.extensions.toCalculatedValue
import rpt.tool.mementobibere.utils.extensions.toMainTheme
import rpt.tool.mementobibere.utils.helpers.SqliteHelper
import java.util.Calendar

class InfoActivity : RPTBaseAppCompatActivity() {

    private var inTook: Float = 0f
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var binding: ActivityInfoBinding
    private lateinit var principal : PartialPrincipalInfoBinding
    private lateinit var partial : PartialInfoBinding
    private lateinit var sharedPref: SharedPreferences
    private var themeInt : Int = 0
    private var unit : Int = 0
    private var currentToneUri: String? = ""
    private var notificMsg: String = ""
    private var notificFrequency: Int = 45
    private var startSleepTime: Long = 0
    private var stopSleepTime: Long = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        setTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        principal = PartialPrincipalInfoBinding.bind(binding.root)
        partial = PartialInfoBinding.bind(binding.root)
        setContentView(binding.root)
        val htmlLegacy =
            getString(R.string.policy_val).replace("\\\'","'")
        val mimeType = "text/html";
        val encoding = "UTF-8";

        val htmlCredits =
            getString(R.string.html_credits).replace("\\\'","'")

        setBackground()
        initBottomBars()

        val is24h = android.text.format.DateFormat.is24HourFormat(this)

        principal.etSendMail.editText!!.setOnClickListener {
            var dialogMail = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
            dialogMail
                .setTitleText(getString(R.string.send_mail))
                .setContentText(getString(R.string.dialog_mail_message))
                .setCancelText("No")
                .setConfirmText("Ok")
                .showCancelButton(true)
                .setCancelClickListener { _ -> // reuse previous dialog instance, keep widget user state, reset them if you need
                    dialogMail.dismiss()

                }
                .setConfirmClickListener { _ ->
                    sendMail()
                }
                .show()
        }



        var stringColor = when(themeInt){
            1->"#41B279"
            2->"#29704D"
            else -> {"#41B279"}
        }

        principal.etCredits.editText!!.setOnClickListener {
            binding.principal.layoutPrincipal.visibility = View.GONE
            binding.partial.layoutPartial.visibility = View.VISIBLE


            binding.partial.web.loadDataWithBaseURL("", htmlCredits, mimeType, encoding, "")
            binding.partial.web.setBackgroundColor(Color.parseColor(stringColor))
        }

        principal.etPrivacy.editText!!.setOnClickListener {
            binding.principal.layoutPrincipal.visibility = View.GONE
            binding.partial.layoutPartial.visibility = View.VISIBLE


            binding.partial.web.loadDataWithBaseURL("", htmlLegacy, mimeType, encoding, "")
            binding.partial.web.setBackgroundColor(Color.parseColor(stringColor))
        }

        binding.partial.btnBack.setOnClickListener {
            binding.principal.layoutPrincipal.visibility = View.VISIBLE
            binding.partial.layoutPartial.visibility = View.GONE
        }

        binding.principal.btnBack.setOnClickListener {
            notificMsg = binding.principal.etNotificationText.editText!!.text.toString()
            if(TextUtils.isEmpty(notificMsg))
            {
              showMessage(getString(R.string.please_a_notification_message),it, true)
            }
            else{
                var editor = sharedPref.edit()
                editor.putString(AppUtils.NOTIFICATION_MSG_KEY, notificMsg)
                editor.putString(AppUtils.NOTIFICATION_TONE_URI_KEY,currentToneUri.toString())
                editor.putLong(AppUtils.START_TIME_KEY, startSleepTime)
                editor.putLong(AppUtils.STOP_TIME_KEY, stopSleepTime)
                editor.apply()
                finish()
            }

        }

        binding.principal.etNotificationText.editText!!.setText(
            sharedPref.getString(
                AppUtils.NOTIFICATION_MSG_KEY,
                getString(R.string.hey_lets_drink_some_water)
            )
        )

        currentToneUri = sharedPref.getString(
            AppUtils.NOTIFICATION_TONE_URI_KEY,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()
        )

        binding.principal.etRingtone.editText!!.setText(
            RingtoneManager.getRingtone(
                this,
                Uri.parse(currentToneUri)
            ).getTitle(this)
        )



        binding.principal.etRingtone.editText!!.setOnClickListener {
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

        startSleepTime = sharedPref.getLong(AppUtils.START_TIME_KEY, sharedPref.getLong(AppUtils.SLEEPING_TIME_KEY, 1558323000000))
        stopSleepTime = sharedPref.getLong(AppUtils.STOP_TIME_KEY, sharedPref.getLong(AppUtils.WAKEUP_TIME_KEY, 1558323000000))

        val calStart = Calendar.getInstance()
        calStart.timeInMillis = startSleepTime
        binding.principal.etStartSleep.editText!!.setText(
            String.format(
                "%02d:%02d",
                calStart.get(Calendar.HOUR_OF_DAY),
                calStart.get(Calendar.MINUTE)
            )
        )

        val calStop = Calendar.getInstance()
        calStop.timeInMillis = stopSleepTime
        binding.principal.etStopSleep.editText!!.setText(
            String.format(
                "%02d:%02d",
                calStop.get(Calendar.HOUR_OF_DAY),
                calStop.get(Calendar.MINUTE)
            )
        )

        binding.principal.etStartSleep.editText!!.setOnClickListener {
            val calendarStart = Calendar.getInstance()
            calendarStart.timeInMillis = startSleepTime

            val mTimePicker = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->

                    val timeStart = Calendar.getInstance()
                    timeStart.set(Calendar.HOUR_OF_DAY, selectedHour)
                    timeStart.set(Calendar.MINUTE, selectedMinute)
                    startSleepTime = timeStart.timeInMillis

                    binding.principal.etStartSleep.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE), is24h
            )
            mTimePicker.setTitle(getString(R.string.select_wakeup_time))
            mTimePicker.show()
        }


        binding.principal.etStopSleep.editText!!.setOnClickListener {
            val calendarStop = Calendar.getInstance()
            calendarStop.timeInMillis = stopSleepTime

            val mTimePicker = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->

                    val timeStop = Calendar.getInstance()
                    timeStop.set(Calendar.HOUR_OF_DAY, selectedHour)
                    timeStop.set(Calendar.MINUTE, selectedMinute)
                    stopSleepTime = timeStop.timeInMillis

                    binding.principal.etStopSleep.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendarStop.get(Calendar.HOUR_OF_DAY), calendarStop.get(Calendar.MINUTE), is24h
            )
            mTimePicker.setTitle(getString(R.string.select_sleeping_time))
            mTimePicker.show()
        }
    }

    private fun setBackground() {
        when(themeInt){
            0-> setBackgroundColor(getColor(R.color.colorSecondaryDark))
            1-> setBackgroundColor(getColor(R.color.darkGreen))

        }
    }

    private fun setBackgroundColor(color: Int) {
        binding.principal.layoutPrincipal.setBackgroundColor(color)
        binding.partial.layoutPartial.setBackgroundColor(color)
    }

    private fun initBottomBars() {
        val menu = principal.darkThemeBottomBar.menu
        val menu2 = principal.unitSystemBottomBar.menu
        val menu3 = principal.notificationBottomBar.menu

        for (i in AppUtils.listIdsInfoTheme.indices) {
            menu.add(
                MenuItemDescriptor.Builder(
                    this,
                    AppUtils.listIdsInfoTheme[i],
                    AppUtils.listInfoTheme[i],
                    AppUtils.listStringInfoTheme[i],
                    Color.parseColor("#41B279")
                )
                    .build()
            )
        }

        for (i in AppUtils.listIdsInfoSystem.indices) {
            menu2.add(
                MenuItemDescriptor.Builder(
                    this,
                    AppUtils.listIdsInfoSystem[i],
                    AppUtils.listInfoSystem[i],
                    AppUtils.listStringInfoSystem[i],
                    Color.parseColor("#41B279")
                )
                    .build()
            )
        }

        for (i in AppUtils.listIdsFreq.indices) {
            menu3.add(
                MenuItemDescriptor.Builder(
                    this,
                    AppUtils.listIdsFreq[i],
                    AppUtils.listFreq[i],
                    AppUtils.listStringFreq[i],
                    Color.parseColor("#41B279")
                )
                    .build()
            )
        }

        principal.darkThemeBottomBar.onItemSelectedListener = { _, i, _ ->
            when(i.id) {
                R.id.icon_light -> themeInt = 0
                R.id.icon_dark -> themeInt = 1
            }

            setThemeToShared()

        }

        principal.unitSystemBottomBar.onItemSelectedListener = { _, i, _ ->
            when(i.id) {
                R.id.icon_ml -> unit = 0
                R.id.icon_oz_uk -> unit = 1
                R.id.icon_oz_us -> unit = 2

            }

            setSystemUnit()

        }

        when (themeInt) {
            0 -> menu.select(R.id.icon_light)
            1 -> menu.select(R.id.icon_dark)
            else -> {
                menu.select(R.id.icon_light)
                themeInt = 0
            }
        }

        unit = sharedPref.getInt(AppUtils.UNIT_KEY,0)

        when (unit) {
            0 -> menu2.select(R.id.icon_ml)
            1 -> menu2.select(R.id.icon_oz_uk)
            2 -> menu2.select(R.id.icon_oz_us)
            else -> {
                menu2.select(R.id.icon_ml)
                unit = 0
            }
        }

        notificFrequency = sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30)
        when (notificFrequency) {
            30 -> menu3.select(R.id.icon_30)
            45 -> menu3.select(R.id.icon_45)
            60 -> menu3.select(R.id.icon_60)
            else -> {
                menu3.select(R.id.icon_30)
                notificFrequency = 30
            }
        }

        principal.notificationBottomBar.onItemSelectedListener = { _, i, _ ->
            when(i.id) {
                R.id.icon_30 -> notificFrequency = 30
                R.id.icon_45 -> notificFrequency = 45
                R.id.icon_60 -> notificFrequency = 60
            }
            sharedPref.edit().putInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, notificFrequency).apply()
        }

    }

    private fun setSystemUnit() {
        sqliteHelper = SqliteHelper(this)
        inTook = sqliteHelper.getIntook(AppUtils.getCurrentOnlyDate()!!)
        if(inTook > 0){
            if(sqliteHelper.resetIntook(AppUtils.getCurrentOnlyDate()!!) == 1){
                sqliteHelper.addIntook(AppUtils.getCurrentOnlyDate()!!,
                    inTook.toCalculatedValue(sharedPref.getInt(UNIT_KEY,0),unit),
                    calculateExtensions(unit))
            }
        }
        val editor = sharedPref.edit()
        editor.putInt(AppUtils.UNIT_NEW_KEY, unit)
        editor.putFloat(AppUtils.VALUE_50_KEY,AppUtils.firstConversion(50f,unit))
        editor.putFloat(AppUtils.VALUE_100_KEY,AppUtils.firstConversion(100f,unit))
        editor.putFloat(AppUtils.VALUE_150_KEY,AppUtils.firstConversion(150f,unit))
        editor.putFloat(AppUtils.VALUE_200_KEY,AppUtils.firstConversion(200f,unit))
        editor.putFloat(AppUtils.VALUE_250_KEY,AppUtils.firstConversion(250f,unit))
        editor.apply()
    }

    private fun setThemeToShared() {
        val editor = sharedPref.edit()
        editor.putInt(AppUtils.THEME_KEY, themeInt)
        editor.apply()
    }

    private fun sendMail() {
        val email = Intent(Intent.ACTION_SENDTO)
        email.data = Uri.parse(getString(R.string.mailto_riccardo_pezzolati_gmail_com))
        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.suggestions_for_memento_bibere))
        email.putExtra(Intent.EXTRA_TEXT, getString(R.string.your_message_here))
        startActivity(email)
    }

    private fun setTheme() {
        val theme = themeInt.toMainTheme()
        setTheme(theme)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 999) {

            val uri = data!!.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI) as Uri
            currentToneUri = uri.toString()
            sharedPref.edit().putString(AppUtils.NOTIFICATION_TONE_URI_KEY, currentToneUri).apply()
            val ringtone = RingtoneManager.getRingtone(this, uri)
            binding.principal.etRingtone.editText!!.setText(ringtone.getTitle(this))

        }
    }
}