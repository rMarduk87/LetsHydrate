package rpt.tool.mementobibere.utils.extensions

import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import java.lang.Float.parseFloat
import java.math.RoundingMode
import java.text.DecimalFormat

fun Int.toMainTheme(): Int {
    when(this){
        0->return R.style.MainTheme
        1->return R.style.MainThemeD
    }
    return R.style.MainTheme
}

fun Int.toAppTheme(): Int {
    when(this){
        0->return R.style.AppTheme
        1->return R.style.AppThemeD
    }
    return R.style.AppTheme
}

fun String.toExtractFloat(): Float{
    val k = this.split(" ")
    val step = k[0].toFloat()
    if(step.length()==1){
        return parseFloat(String.format("%.0f",step))
    }
    return step
}

fun Float.toCalculatedValueStats(current: Int, newValue: Int): Float {
    return if(current == 0){
        this.toCalculatedValue(0,newValue)
    }
    else{
        val mlStep = this.toCalculatedValue(current,0)
        mlStep.toCalculatedValue(0,newValue)
        return mlStep
    }
}

fun Float.toCalculatedValue(current: Int, newValue: Int) : Float{
    if(current == 0 && newValue == 1) {
        return AppUtils.mlToOzUK(this)
    }
    if(current == 0 && newValue == 2){
        return AppUtils.mlToOzUS(this)
    }
    if(current == 1 && newValue == 0) {
        return AppUtils.ozUKToMl(this)
    }
    if(current == 1 && newValue == 2) {
        return AppUtils.ozUKToOzUS(this)
    }
    if(current == 2 && newValue == 1) {
        return AppUtils.ozUSToozUK(this)
    }
    if(current == 2 && newValue == 0) {
        return AppUtils.ozUSToMl(this)
    }
    return this
}


fun Int.toPrincipalUnit(weightUnit: Int): Int {
    if(weightUnit == 1) {
        return AppUtils.lblToKg(this)
    }
    return this
}

fun Int.toCalculateWeight(weightUnit: Int): Int {
    if(weightUnit == 1) {
        return AppUtils.kgToLbl(this)
    }
    return this
}

fun Float.toNumberString(): String {
    var pattern = "#.##"
    val length = this.length()
    if(length == 1){
        pattern = "#"
    }

    return DecimalFormat(pattern)
        .apply { roundingMode = RoundingMode.FLOOR }
        .format(this).replace(",",".")
}


fun Float.length(): Int {
    val string = this.toString()
    return string.length - (string.indexOf('.') + 1)
}

