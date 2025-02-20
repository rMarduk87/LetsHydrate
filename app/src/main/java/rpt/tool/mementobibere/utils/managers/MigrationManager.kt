package rpt.tool.mementobibere.utils.managers

import rpt.tool.mementobibere.utils.AppUtils

class MigrationManager {
    fun migrate() {
        if(SharedPreferencesManager.isMigration){
            val weight = SharedPreferencesManager.weight.toString()
            val climate = SharedPreferencesManager.climate
            val workType = SharedPreferencesManager.workType
            val totalIntake = SharedPreferencesManager.totalIntake

            SharedPreferencesManager.personWeight = weight
            SharedPreferencesManager.personWeightUnit = SharedPreferencesManager.weightUnit==0
            SharedPreferencesManager.unitString = if (SharedPreferencesManager.personWeightUnit)
                "ml" else "fl oz"
            SharedPreferencesManager.climate = convertClimate(climate)
            SharedPreferencesManager.workType = convertWorkType(workType)
            AppUtils.DAILY_WATER_VALUE = totalIntake
            AppUtils.WATER_UNIT = SharedPreferencesManager.unitString
            SharedPreferencesManager.hideWelcomeScreen = true
            removeShared()
        }
    }

    private fun removeShared() {
        SharedPreferencesManager.removeShared(AppUtils.THEME_KEY)
        SharedPreferencesManager.removeShared(AppUtils.UNIT_KEY)
        SharedPreferencesManager.removeShared(AppUtils.UNIT_NEW_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_50_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_100_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_150_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_200_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_250_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_300_KEY)
        SharedPreferencesManager.removeShared(AppUtils.VALUE_350_KEY)
        SharedPreferencesManager.removeShared(AppUtils.FIRST_RUN_KEY)
        SharedPreferencesManager.removeShared(AppUtils.LAST_INTOOK_KEY)
        SharedPreferencesManager.removeShared(AppUtils.NO_UPDATE_UNIT)
        SharedPreferencesManager.removeShared(AppUtils.RESET_NOTIFICATION_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SET_GENDER_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SET_BLOOD_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SET_NEW_WORK_TYPE_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SET_CLIMATE_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SET_WEIGHT_UNIT)
        SharedPreferencesManager.removeShared(AppUtils.START_TUTORIAL_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SEE_SPLASH_KEY)
        SharedPreferencesManager.removeShared(AppUtils.STAT_IS_MONTH_KEY)
        SharedPreferencesManager.removeShared(AppUtils.INDEX_MONTH_KEY)
        SharedPreferencesManager.removeShared(AppUtils.INDEX_YEAR_KEY)
        SharedPreferencesManager.removeShared(AppUtils.SEE_TIPS_KEY)
        SharedPreferencesManager.removeShared(AppUtils.NOTIFICATION_TONE_URI_KEY)
    }

    private fun convertWorkType(workType: Int): Int {
        if(workType==0){
            return  0
        }
        return 1
    }

    private fun convertClimate(climate: Int): Int {
        var weather = 0
        when(climate){
            0->weather = 3 //from cold to snow
            1->weather = 1 //from fresh to cloudy
        }

        return weather
    }
}