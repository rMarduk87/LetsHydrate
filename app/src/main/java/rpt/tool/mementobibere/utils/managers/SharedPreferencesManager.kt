package rpt.tool.mementobibere.utils.managers

import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import rpt.tool.mementobibere.Application
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils

object SharedPreferencesManager {
    private val ctx: Context
        get() = Application.instance

    private fun createSharedPreferences(): SharedPreferences {
        return ctx.getSharedPreferences(AppUtils.USERS_SHARED_PREF, Context.MODE_PRIVATE)
    }

    private val sharedPreferences by lazy { createSharedPreferences() }

    var themeInt: Int
        get() = sharedPreferences.getInt(AppUtils.THEME_KEY,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.THEME_KEY, value).apply()
    var current_unitInt: Int
        get() = sharedPreferences.getInt(AppUtils.UNIT_KEY,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.UNIT_KEY, value).apply()
    var new_unitInt: Int
        get() = sharedPreferences.getInt(AppUtils.UNIT_NEW_KEY,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.UNIT_NEW_KEY, value).apply()
    var value_50: Float
        get() = sharedPreferences.getFloat(AppUtils.VALUE_50_KEY,100f)
        set(value) = sharedPreferences.edit().putFloat(AppUtils.VALUE_50_KEY, value).apply()

    var value_100: Float
        get() = sharedPreferences.getFloat(AppUtils.VALUE_100_KEY,50f)
        set(value) = sharedPreferences.edit().putFloat(AppUtils.VALUE_100_KEY, value).apply()
    var value_150: Float
        get() = sharedPreferences.getFloat(AppUtils.VALUE_150_KEY,150f)
        set(value) = sharedPreferences.edit().putFloat(AppUtils.VALUE_150_KEY, value).apply()

    var value_200: Float
        get() = sharedPreferences.getFloat(AppUtils.VALUE_200_KEY,200f)
        set(value) = sharedPreferences.edit().putFloat(AppUtils.VALUE_200_KEY, value).apply()

    var value_250: Float
        get() = sharedPreferences.getFloat(AppUtils.VALUE_250_KEY,250f)
        set(value) = sharedPreferences.edit().putFloat(AppUtils.VALUE_250_KEY, value).apply()
    var value_300: Float
        get() = sharedPreferences.getFloat(AppUtils.VALUE_300_KEY,300f)
        set(value) = sharedPreferences.edit().putFloat(AppUtils.VALUE_300_KEY, value).apply()
    var value_350: Float
        get() = sharedPreferences.getFloat(AppUtils.VALUE_350_KEY,350f)
        set(value) = sharedPreferences.edit().putFloat(AppUtils.VALUE_350_KEY, value).apply()
    var bloodDonorKey: Int
        get() = sharedPreferences.getInt(AppUtils.BLOOD_DONOR_KEY,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.BLOOD_DONOR_KEY, value).apply()
    var totalIntake: Float
        get() = sharedPreferences.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
        set(value) = sharedPreferences.edit().putFloat(AppUtils.TOTAL_INTAKE_KEY, value).apply()
    var firstRun: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.FIRST_RUN_KEY, true)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.FIRST_RUN_KEY, value).apply()
    var unitString: String
        get() = sharedPreferences.getString(AppUtils.UNIT_STRING,"ml")!!
        set(value) = sharedPreferences.edit().putString(AppUtils.UNIT_STRING, value).apply()
    var lastIntook: Float
        get() = sharedPreferences.getFloat(AppUtils.LAST_INTOOK_KEY, 0f)
        set(value) = sharedPreferences.edit().putFloat(AppUtils.LAST_INTOOK_KEY, value).apply()
    var notificationFreq: Float
        get() = sharedPreferences.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toFloat()
        set(value) = sharedPreferences.edit().putInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, value.toInt()).apply()
    var noUpdateUnit: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.NO_UPDATE_UNIT, false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.NO_UPDATE_UNIT, value).apply()
    var resetNotification: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.RESET_NOTIFICATION_KEY,true)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.RESET_NOTIFICATION_KEY, value).apply()
    var setGender: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SET_GENDER_KEY, false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.SET_GENDER_KEY, value).apply()
    var setBloodDonor: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SET_BLOOD_KEY, false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.SET_BLOOD_KEY, value).apply()
    var setWorkOut: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SET_NEW_WORK_TYPE_KEY, false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.SET_NEW_WORK_TYPE_KEY, value).apply()
    var setClimate: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SET_CLIMATE_KEY, false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.SET_CLIMATE_KEY, value).apply()
    var setWeight: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SET_WEIGHT_UNIT, false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.SET_WEIGHT_UNIT, value).apply()
    var sleepingTime: Long
        get() = sharedPreferences.getLong(AppUtils.SLEEPING_TIME_KEY,1558369800000)
        set(value) = sharedPreferences.edit().putLong(AppUtils.SLEEPING_TIME_KEY, value).apply()
    var wakeUpTime: Long
        get() = sharedPreferences.getLong(AppUtils.WAKEUP_TIME_KEY,1558323000000)
        set(value) = sharedPreferences.edit().putLong(AppUtils.WAKEUP_TIME_KEY, value).apply()
    var notificationStatus: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.NOTIFICATION_STATUS_KEY, true)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.NOTIFICATION_STATUS_KEY, value).apply()
    var setTips : Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SEE_TIPS_KEY,true)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.SEE_TIPS_KEY, value).apply()
    var startTutorial: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.START_TUTORIAL_KEY,false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.START_TUTORIAL_KEY, value).apply()
    var gender: Int
        get() = sharedPreferences.getInt(AppUtils.GENDER_KEY, 0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.GENDER_KEY, value).apply()
    var workType: Int
        get() = sharedPreferences.getInt(AppUtils.WORK_TIME_KEY, 0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.WORK_TIME_KEY, value).apply()
    var weight: Int
        get() = sharedPreferences.getInt(AppUtils.WEIGHT_KEY, 0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.WEIGHT_KEY, value).apply()
    var weightUnit: Int
        get() = sharedPreferences.getInt(AppUtils.WEIGHT_UNIT_KEY,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.WEIGHT_UNIT_KEY, value).apply()
    var climate: Int
        get() = sharedPreferences.getInt(AppUtils.CLIMATE_KEY, 0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.CLIMATE_KEY, value).apply()
    var notificationMsg: String
        get() = sharedPreferences.getString(AppUtils.NOTIFICATION_MSG_KEY,ctx.getString(R.string.hey_lets_drink_some_water))!!
        set(value) = sharedPreferences.edit().putString(AppUtils.NOTIFICATION_MSG_KEY, value).apply()
    var notificationTone: String
        get() = sharedPreferences.getString(AppUtils.NOTIFICATION_TONE_URI_KEY, RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_NOTIFICATION).toString())!!
        set(value) = sharedPreferences.edit().putString(AppUtils.NOTIFICATION_TONE_URI_KEY, value).apply()
    var showSplashScreen: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SEE_SPLASH_KEY,true)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.SEE_SPLASH_KEY, value).apply()
    var date: String
        get() = sharedPreferences.getString(AppUtils.DATE,AppUtils.getCurrentDate()!!)!!
        set(value) = sharedPreferences.edit().putString(AppUtils.DATE, value).apply()
    var isMonth: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.STAT_IS_MONTH_KEY,true)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.STAT_IS_MONTH_KEY, value).apply()
    var indexMonth: Int
        get() = sharedPreferences.getInt(AppUtils.INDEX_MONTH_KEY,-1)
        set(value) = sharedPreferences.edit().putInt(AppUtils.INDEX_MONTH_KEY, value).apply()
    var indexYear: Int
        get() = sharedPreferences.getInt(AppUtils.INDEX_YEAR_KEY,-1)
        set(value) = sharedPreferences.edit().putInt(AppUtils.INDEX_YEAR_KEY, value).apply()
    var hideWelcomeScreen: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.HIDE_WELCOME_SCREEN,false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.HIDE_WELCOME_SCREEN, value).apply()
    var personWeightUnit: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.PERSON_WEIGHT_UNIT,true)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.PERSON_WEIGHT_UNIT, value).apply()
    var personWeight: String
        get() = sharedPreferences.getString(AppUtils.PERSON_WEIGHT, "").toString()
        set(value) = sharedPreferences.edit().putString(AppUtils.PERSON_WEIGHT, value).apply()
    var userName: String
        get() = sharedPreferences.getString(AppUtils.USER_NAME, "").toString()
        set(value) = sharedPreferences.edit().putString(AppUtils.USER_NAME, value).apply()
    var personHeight: String
        get() = sharedPreferences.getString(AppUtils.PERSON_HEIGHT, "").toString()
        set(value) = sharedPreferences.edit().putString(AppUtils.PERSON_HEIGHT, value).apply()
    var wakeUpTimeNew: String
        get() = sharedPreferences.getString(AppUtils.WAKE_UP_TIME, "").toString()
        set(value) = sharedPreferences.edit().putString(AppUtils.WAKE_UP_TIME, value).apply()
    var bedTime: String
        get() = sharedPreferences.getString(AppUtils.BED_TIME, "").toString()
        set(value) = sharedPreferences.edit().putString(AppUtils.BED_TIME, value).apply()
    var ignoreNextStep: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IGNORE_NEXT_STEP,false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.IGNORE_NEXT_STEP, value).apply()
    var isMigration: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_MIGRATION,true)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.IS_MIGRATION, value).apply()
    var setManuallyGoal: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SET_MANUALLY_GOAL,false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.SET_MANUALLY_GOAL, value).apply()
    var setManuallyGoalValue: Float
        get() = sharedPreferences.getFloat(AppUtils.SET_MANUALLY_GOAL_VALUE,0f)
        set(value) = sharedPreferences.edit().putFloat(AppUtils.SET_MANUALLY_GOAL_VALUE, value).apply()
    var isPregnant: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_PREGNANT,false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.IS_PREGNANT, value).apply()
    var isBreastfeeding: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_BREASTFEEDING,false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.IS_BREASTFEEDING, value).apply()
    var personHeightUnit: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.PERSON_HEIGHT_UNIT,true)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.PERSON_HEIGHT_UNIT, value).apply()
}