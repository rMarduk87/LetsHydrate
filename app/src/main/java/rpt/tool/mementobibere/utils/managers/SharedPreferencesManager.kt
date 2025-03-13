package rpt.tool.mementobibere.utils.managers

import android.content.Context
import android.content.SharedPreferences
import rpt.tool.mementobibere.Application
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import androidx.core.content.edit

object SharedPreferencesManager {
    private val ctx: Context
        get() = Application.instance

    private fun createSharedPreferences(): SharedPreferences {
        return ctx.getSharedPreferences(AppUtils.USERS_SHARED_PREF, Context.MODE_PRIVATE)
    }

    private val sharedPreferences by lazy { createSharedPreferences() }

    fun removeShared(key:String){
        sharedPreferences.edit() { remove(key) }
    }

    var bloodDonorKey: Int
        get() = sharedPreferences.getInt(AppUtils.BLOOD_DONOR_KEY,0)
        set(value) = sharedPreferences.edit() { putInt(AppUtils.BLOOD_DONOR_KEY, value) }
    var totalIntake: Float
        get() = sharedPreferences.getFloat(AppUtils.TOTAL_INTAKE_KEY, 0f)
        set(value) = sharedPreferences.edit() {putFloat(AppUtils.TOTAL_INTAKE_KEY, value) }
    var unitString: String
        get() = sharedPreferences.getString(AppUtils.UNIT_STRING,"ml")!!
        set(value) = sharedPreferences.edit() {putString(AppUtils.UNIT_STRING, value) }
    var notificationFreq: Float
        get() = sharedPreferences.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toFloat()
        set(value) = sharedPreferences.edit() {putInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, value.toInt()) }
    var sleepingTime: Long
        get() = sharedPreferences.getLong(AppUtils.SLEEPING_TIME_KEY,1558369800000)
        set(value) = sharedPreferences.edit() {putLong(AppUtils.SLEEPING_TIME_KEY, value) }
    var wakeUpTime: Long
        get() = sharedPreferences.getLong(AppUtils.WAKEUP_TIME_KEY,1558323000000)
        set(value) = sharedPreferences.edit() {putLong(AppUtils.WAKEUP_TIME_KEY, value) }
    var notificationStatus: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.NOTIFICATION_STATUS_KEY, true)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.NOTIFICATION_STATUS_KEY, value) }
    var gender: Int
        get() = sharedPreferences.getInt(AppUtils.GENDER_KEY, 0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.GENDER_KEY, value) }
    var workType: Int
        get() = sharedPreferences.getInt(AppUtils.WORK_TIME_KEY, 0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.WORK_TIME_KEY, value) }
    var weight: Int
        get() = sharedPreferences.getInt(AppUtils.WEIGHT_KEY, 0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.WEIGHT_KEY, value) }
    var weightUnit: Int
        get() = sharedPreferences.getInt(AppUtils.WEIGHT_UNIT_KEY,0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.WEIGHT_UNIT_KEY, value) }
    var climate: Int
        get() = sharedPreferences.getInt(AppUtils.CLIMATE_KEY, 0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.CLIMATE_KEY, value) }
    var notificationMsg: String
        get() = sharedPreferences.getString(AppUtils.NOTIFICATION_MSG_KEY,ctx.getString(R.string.hey_lets_drink_some_water))!!
        set(value) = sharedPreferences.edit() {putString(AppUtils.NOTIFICATION_MSG_KEY, value) }
    var date: String
        get() = sharedPreferences.getString(AppUtils.DATE,AppUtils.getCurrentDate()!!)!!
        set(value) = sharedPreferences.edit() {putString(AppUtils.DATE, value) }
    var hideWelcomeScreen: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.HIDE_WELCOME_SCREEN,false)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.HIDE_WELCOME_SCREEN, value) }
    var personWeightUnit: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.PERSON_WEIGHT_UNIT,true)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.PERSON_WEIGHT_UNIT, value) }
    var personWeight: String
        get() = sharedPreferences.getString(AppUtils.PERSON_WEIGHT, "").toString()
        set(value) = sharedPreferences.edit() {putString(AppUtils.PERSON_WEIGHT, value) }
    var userName: String
        get() = sharedPreferences.getString(AppUtils.USER_NAME, "").toString()
        set(value) = sharedPreferences.edit() {putString(AppUtils.USER_NAME, value) }
    var personHeight: String
        get() = sharedPreferences.getString(AppUtils.PERSON_HEIGHT, "").toString()
        set(value) = sharedPreferences.edit() {putString(AppUtils.PERSON_HEIGHT, value) }
    var ignoreNextStep: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IGNORE_NEXT_STEP,false)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.IGNORE_NEXT_STEP, value) }
    var isMigration: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_MIGRATION,false)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.IS_MIGRATION, value) }
    var setManuallyGoal: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SET_MANUALLY_GOAL,false)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.SET_MANUALLY_GOAL, value) }
    var setManuallyGoalValue: Float
        get() = sharedPreferences.getFloat(AppUtils.SET_MANUALLY_GOAL_VALUE,0f)
        set(value) = sharedPreferences.edit() {putFloat(AppUtils.SET_MANUALLY_GOAL_VALUE, value) }
    var isPregnant: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_PREGNANT,false)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.IS_PREGNANT, value) }
    var isBreastfeeding: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_BREASTFEEDING,false)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.IS_BREASTFEEDING, value) }
    var personHeightUnit: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.PERSON_HEIGHT_UNIT,true)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.PERSON_HEIGHT_UNIT, value) }
    var selectedContainer: Int
        get() = sharedPreferences.getInt(AppUtils.SELECTED_CONTAINER,0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.SELECTED_CONTAINER, value) }
    var disableSoundWhenAddWater: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.DISABLE_SOUND_WHEN_ADD_WATER,false)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.DISABLE_SOUND_WHEN_ADD_WATER, value) }
    var menu: Int
        get() = sharedPreferences.getInt(AppUtils.MENU,0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.MENU, value) }
    var reminderSound: Int
        get() = sharedPreferences.getInt(AppUtils.REMINDER_SOUND,0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.REMINDER_SOUND, value) }
    var disableNotificationAtGoal: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.DISABLE_NOTIFICATION,false)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.DISABLE_NOTIFICATION, value) }
    var wakeUpTimeNew: String
        get() = sharedPreferences.getString(AppUtils.WAKE_UP_TIME, "").toString()
        set(value) = sharedPreferences.edit() {putString(AppUtils.WAKE_UP_TIME, value) }
    var wakeUpTimeHour: Int
        get() = sharedPreferences.getInt(AppUtils.WAKE_UP_TIME_HOUR,0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.WAKE_UP_TIME_HOUR, value) }
    var wakeUpTimeMinute: Int
        get() = sharedPreferences.getInt(AppUtils.WAKE_UP_TIME_MINUTE,0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.WAKE_UP_TIME_MINUTE, value) }
    var bedTime: String
        get() = sharedPreferences.getString(AppUtils.BED_TIME, "").toString()
        set(value) = sharedPreferences.edit() {putString(AppUtils.BED_TIME, value) }
    var bedTimeHour: Int
        get() = sharedPreferences.getInt(AppUtils.BED_TIME_HOUR,0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.BED_TIME_HOUR, value) }
    var bedTimeMinute: Int
        get() = sharedPreferences.getInt(AppUtils.BED_TIME_MINUTE,0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.BED_TIME_MINUTE, value) }
    var reminderOpt: Int
        get() = sharedPreferences.getInt(AppUtils.REMINDER_OPTION,0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.REMINDER_OPTION, value) }
    var reminderVibrate: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.REMINDER_VIBRATE,true)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.REMINDER_VIBRATE, value) }
    var isManualReminder: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_MANUAL_REMINDER,false)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.IS_MANUAL_REMINDER, value) }
    var isNewAlarmSystem: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_NEW_ALARM_SYSTEM,true)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.IS_NEW_ALARM_SYSTEM, value) }
    var autoBackUp: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.AUTO_BACK_UP,true)
        set(value) = sharedPreferences.edit() {putBoolean(AppUtils.AUTO_BACK_UP, value) }
    var autoBackUpType: Int
        get() = sharedPreferences.getInt(AppUtils.AUTO_BACK_UP_TYPE,0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.AUTO_BACK_UP_TYPE, value) }
    var autoBackUpId: Int
        get() = sharedPreferences.getInt(AppUtils.AUTO_BACK_UP_ID,0)
        set(value) = sharedPreferences.edit() {putInt(AppUtils.AUTO_BACK_UP_ID, value) }
}