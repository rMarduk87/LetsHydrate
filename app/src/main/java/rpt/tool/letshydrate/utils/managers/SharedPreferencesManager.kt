package rpt.tool.letshydrate.utils.managers

import android.content.Context
import android.content.SharedPreferences
import rpt.tool.letshydrate.Application
import rpt.tool.letshydrate.R
import rpt.tool.letshydrate.utils.AppUtils

object SharedPreferencesManager {
    private val ctx: Context
        get() = Application.instance

    private fun createSharedPreferences(): SharedPreferences {
        return ctx.getSharedPreferences(AppUtils.USERS_SHARED_PREF, Context.MODE_PRIVATE)
    }

    private val sharedPreferences by lazy { createSharedPreferences() }

    fun removeShared(key:String){
        sharedPreferences.edit().remove(key).apply()
    }

    var bloodDonorKey: Int
        get() = sharedPreferences.getInt(AppUtils.BLOOD_DONOR_KEY,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.BLOOD_DONOR_KEY, value).apply()
    var totalIntake: Float
        get() = sharedPreferences.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
        set(value) = sharedPreferences.edit().putFloat(AppUtils.TOTAL_INTAKE_KEY, value).apply()
    var unitString: String
        get() = sharedPreferences.getString(AppUtils.UNIT_STRING,"ml")!!
        set(value) = sharedPreferences.edit().putString(AppUtils.UNIT_STRING, value).apply()
    var notificationFreq: Float
        get() = sharedPreferences.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toFloat()
        set(value) = sharedPreferences.edit().putInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, value.toInt()).apply()
    var sleepingTime: Long
        get() = sharedPreferences.getLong(AppUtils.SLEEPING_TIME_KEY,1558369800000)
        set(value) = sharedPreferences.edit().putLong(AppUtils.SLEEPING_TIME_KEY, value).apply()
    var wakeUpTime: Long
        get() = sharedPreferences.getLong(AppUtils.WAKEUP_TIME_KEY,1558323000000)
        set(value) = sharedPreferences.edit().putLong(AppUtils.WAKEUP_TIME_KEY, value).apply()
    var notificationStatus: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.NOTIFICATION_STATUS_KEY, true)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.NOTIFICATION_STATUS_KEY, value).apply()
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
    var date: String
        get() = sharedPreferences.getString(AppUtils.DATE,AppUtils.getCurrentDate()!!)!!
        set(value) = sharedPreferences.edit().putString(AppUtils.DATE, value).apply()
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
    var ignoreNextStep: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IGNORE_NEXT_STEP,false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.IGNORE_NEXT_STEP, value).apply()
    var isMigration: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_MIGRATION,false)
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
    var selectedContainer: Int
        get() = sharedPreferences.getInt(AppUtils.SELECTED_CONTAINER,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.SELECTED_CONTAINER, value).apply()
    var disableSoundWhenAddWater: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.DISABLE_SOUND_WHEN_ADD_WATER,false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.DISABLE_SOUND_WHEN_ADD_WATER, value).apply()
    var menu: Int
        get() = sharedPreferences.getInt(AppUtils.MENU,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.MENU, value).apply()
    var reminderSound: Int
        get() = sharedPreferences.getInt(AppUtils.REMINDER_SOUND,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.REMINDER_SOUND, value).apply()
    var disableNotificationAtGoal: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.DISABLE_NOTIFICATION,false)
        set(value) = sharedPreferences.edit().putBoolean(AppUtils.DISABLE_NOTIFICATION, value).apply()
    var wakeUpTimeNew: String
        get() = sharedPreferences.getString(AppUtils.WAKE_UP_TIME, "").toString()
        set(value) = sharedPreferences.edit().putString(AppUtils.WAKE_UP_TIME, value).apply()
    var wakeUpTimeHour: Int
        get() = sharedPreferences.getInt(AppUtils.WAKE_UP_TIME_HOUR,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.WAKE_UP_TIME_HOUR, value).apply()
    var wakeUpTimeMinute: Int
        get() = sharedPreferences.getInt(AppUtils.WAKE_UP_TIME_MINUTE,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.WAKE_UP_TIME_MINUTE, value).apply()
    var bedTime: String
        get() = sharedPreferences.getString(AppUtils.BED_TIME, "").toString()
        set(value) = sharedPreferences.edit().putString(AppUtils.BED_TIME, value).apply()
    var bedTimeHour: Int
        get() = sharedPreferences.getInt(AppUtils.BED_TIME_HOUR,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.BED_TIME_HOUR, value).apply()
    var bedTimeMinute: Int
        get() = sharedPreferences.getInt(AppUtils.BED_TIME_MINUTE,0)
        set(value) = sharedPreferences.edit().putInt(AppUtils.BED_TIME_MINUTE, value).apply()

}