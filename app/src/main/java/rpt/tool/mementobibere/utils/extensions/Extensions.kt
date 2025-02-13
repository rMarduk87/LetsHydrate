package rpt.tool.mementobibere.utils.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import java.lang.Float.parseFloat
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Calendar
import java.util.Date
import java.util.Locale
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

fun String.toExtractFloat(isAll : Boolean = false): Float{
    val k = this.split(" ")
    var j = k[0]
    if(isAll){
        j = k[0].split("/")[1]
    }
    val step = j.toFloat()
    if(step.length()==1){
        return parseFloat(String.format("%.0f",step))
    }
    return step
}

fun Float.toStats(): Float{
    return String.format("%.0f",this).toFloat()
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

fun Date.toStringHour(): String {
    val df = SimpleDateFormat("hh:mm")
    return df.format(this)
}

fun Int.toExtractIntookOption(unit: Int): String {
    var result = ""
    when(unit){
        0-> result = when(this){
            0-> "50"
            1-> "100"
            2-> "150"
            3-> "200"
            4-> "250"
            5-> "300"
            6-> "350"
            7-> "custom"
            else->"multi opt."
        }
        1-> result = when(this){
            0-> "1.75"
            1-> "3.51"
            2-> "5.27"
            3-> "7.03"
            4-> "8.79"
            5-> "10.56"
            6-> "12.32"
            7-> "custom"
            else->"multi opt."
        }
        2-> result = when(this){
            0-> "1.69"
            1-> "3.38"
            2-> "5.07"
            3-> "6.76"
            4-> "8.45"
            5-> "10.14"
            6-> "11.83"
            7-> "custom"
            else->"multi opt."
        }
    }

    return result;
}

fun Float?.toDefaultFloatIfNull(): Float {
    return this ?: 0F
}

fun <T : IItem<*>> RecyclerView.defaultSetUp(
    fastAdapter: FastAdapter<T>
) {
    this.adapter = fastAdapter
    this.layoutManager = LinearLayoutManager(context)
    setHasFixedSize(true)
}

fun Float.toReachedStatsString(): String {
    return "${this.toNumberString()}"
}

fun String.toCalendar(): Calendar {
    var calendarString = this.split("-")
    var calendar = Calendar.getInstance()
    calendar.set(calendarString[2].toInt(),calendarString[1].toInt(),calendarString[0].toInt())
    return calendar
}

fun Calendar.toStringDate(): String? {
    this.add(Calendar.MONTH,-1)
    val c = this.time
    val df = SimpleDateFormat("dd-MM-yyyy")
    return df.format(c)
}

fun String.toYear(): String {
    var split = this.split("-")
    return split[2]
}

fun String.toMonth(): String {
    var split = this.split("-")
    if(split[1].startsWith("0")){
        return split[1].removePrefix("0")
    }
    return split[1]
}
