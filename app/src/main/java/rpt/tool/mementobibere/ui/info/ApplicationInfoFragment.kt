package rpt.tool.mementobibere.ui.info

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import rpt.tool.mementobibere.BaseFragment
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.ApplicationInfoFragmentBinding
import rpt.tool.mementobibere.databinding.PartialPrincipalInfoBinding
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.extensions.toCalculatedValue
import rpt.tool.mementobibere.utils.helpers.SqliteHelper

class ApplicationInfoFragment:
    BaseFragment<ApplicationInfoFragmentBinding>(ApplicationInfoFragmentBinding::inflate) {

    private var stringColor: String = ""
    private var inTook: Float = 0f
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var principal : PartialPrincipalInfoBinding
    private lateinit var sharedPref: SharedPreferences
    private var themeInt : Int = 0
    private var unit : Int = 0
    private var currentToneUri: String? = ""
    private var notificMsg: String = ""
    private var notificFrequency: Int = 45

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPref = requireActivity().getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        themeInt = sharedPref.getInt(AppUtils.THEME_KEY,0)
        super.onViewCreated(view, savedInstanceState)
        principal = PartialPrincipalInfoBinding.bind(binding.root)
        val htmlLegacy = "https://www.termsfeed.com/live/d1615b20-2bc9-4048-8b73-b674c2aeb1c5"


        stringColor = when(themeInt){
            1->"#41B279"
            2->"#29704D"
            else -> {"#41B279"}
        }

        setBackground()
        initBottomBars()

        android.text.format.DateFormat.is24HourFormat(requireContext())




        principal.etPrivacy.editText!!.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(htmlLegacy))
            startActivity(browserIntent)
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
                requireContext(),
                Uri.parse(currentToneUri)
            ).getTitle(requireContext())
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
        val editor = sharedPref.edit()

        if(sharedPref.contains(AppUtils.START_TIME_KEY)){
            editor.remove(AppUtils.START_TIME_KEY)
        }

        if(sharedPref.contains(AppUtils.STOP_TIME_KEY)){
            editor.remove(AppUtils.STOP_TIME_KEY)
        }
        editor.apply()

        binding.principal.btnUpdate.setOnClickListener {
            notificMsg = binding.principal.etNotificationText.editText!!.text.toString()
            if(TextUtils.isEmpty(notificMsg))
            {
                showMessage(getString(R.string.please_a_notification_message),it, true)
            }
            else{
                var editor = sharedPref.edit()
                editor.putString(AppUtils.NOTIFICATION_MSG_KEY, notificMsg)
                editor.putString(AppUtils.NOTIFICATION_TONE_URI_KEY,currentToneUri.toString())
                editor.apply()
            }
        }
    }

    private fun setBackground() {
        when(themeInt){
            0-> toLightTheme()
            1-> toDarkTheme()
        }
    }

    private fun toDarkTheme() {
        setBackgroundColor(requireContext().getColor(R.color.darkGreen))
        binding.principal.infoTopTitle.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.principal.darkTV.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.principal.systemUnitTV.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.principal.notificationTV.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.principal.legal.setTextColor(requireContext().getColor(R.color.colorBlack))
        binding.principal.notificationBottomBar.setBackgroundColorRes(R.color.gray_btn_bg_pressed_color)
        binding.principal.unitSystemBottomBar.setBackgroundColorRes(R.color.gray_btn_bg_pressed_color)
        binding.principal.darkThemeBottomBar.setBackgroundColorRes(R.color.gray_btn_bg_pressed_color)
        binding.principal.etNotificationText.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.principal.etRingtone.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.principal.etPrivacy.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.principal.etNotificationText.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.principal.etRingtone.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.principal.etPrivacy.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.principal.view.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.principal.view3.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.principal.view4.
        setBackgroundColor(requireContext().getColor(R.color.gray_btn_bg_pressed_color))
        binding.principal.btnUpdate.setTextColor(requireContext().getColor(R.color.colorBlack))
    }

    private fun toLightTheme() {
        setBackgroundColor(requireContext().getColor(R.color.colorSecondaryDark))
        binding.principal.infoTopTitle.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.darkTV.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.systemUnitTV.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.notificationTV.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.legal.setTextColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.notificationBottomBar.setBackgroundColorRes(R.color.colorWhite)
        binding.principal.unitSystemBottomBar.setBackgroundColorRes(R.color.colorWhite)
        binding.principal.darkThemeBottomBar.setBackgroundColorRes(R.color.colorWhite)
        binding.principal.etNotificationText.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.etRingtone.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.etPrivacy.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.etNotificationText.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.etRingtone.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.etPrivacy.editText!!.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.view.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.view3.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.view4.
        setBackgroundColor(requireContext().getColor(R.color.colorWhite))
        binding.principal.btnUpdate.setTextColor(requireContext().getColor(R.color.colorWhite))
    }

    private fun setBackgroundColor(color: Int) {
        binding.principal.layoutPrincipal.setBackgroundColor(color)
    }

    private fun initBottomBars() {
        val menu = principal.darkThemeBottomBar.menu
        val menu2 = principal.unitSystemBottomBar.menu
        val menu3 = principal.notificationBottomBar.menu

        for (i in AppUtils.listIdsInfoTheme.indices) {
            menu.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIdsInfoTheme[i],
                    AppUtils.listInfoTheme[i],
                    AppUtils.listStringInfoTheme[i],
                    Color.parseColor(stringColor)
                )
                    .build()
            )
        }

        for (i in AppUtils.listIdsInfoSystem.indices) {
            menu2.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIdsInfoSystem[i],
                    AppUtils.listInfoSystem[i],
                    AppUtils.listStringInfoSystem[i],
                    Color.parseColor(stringColor)
                )
                    .build()
            )
        }

        for (i in AppUtils.listIdsFreq.indices) {
            menu3.add(
                MenuItemDescriptor.Builder(
                    requireContext(),
                    AppUtils.listIdsFreq[i],
                    AppUtils.listFreq[i],
                    AppUtils.listStringFreq[i],
                    Color.parseColor(stringColor)
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
        sqliteHelper = SqliteHelper(requireContext())
        inTook = sqliteHelper.getIntook(AppUtils.getCurrentOnlyDate()!!)
        if(inTook > 0){
            if(sqliteHelper.resetIntook(AppUtils.getCurrentOnlyDate()!!) == 1){
                sqliteHelper.addIntook(AppUtils.getCurrentOnlyDate()!!,
                    inTook.toCalculatedValue(sharedPref.getInt(AppUtils.UNIT_KEY,0),unit),
                    AppUtils.calculateExtensions(unit)
                )
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
        setBackground()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 999) {

            val uri = data!!.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI) as Uri
            currentToneUri = uri.toString()
            sharedPref.edit().putString(AppUtils.NOTIFICATION_TONE_URI_KEY, currentToneUri).apply()
            val ringtone = RingtoneManager.getRingtone(requireContext(), uri)
            binding.principal.etRingtone.editText!!.setText(ringtone.getTitle(requireContext()))

        }
    }

}