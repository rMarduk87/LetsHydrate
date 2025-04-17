package rpt.tool.mementobibere.utils.extensions

import android.annotation.SuppressLint
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import rpt.tool.mementobibere.utils.AppUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible


fun Float.length(): Int {
    val string = this.toString()
    return string.length - (string.indexOf('.') + 1)
}


fun String.toYear(): String {
    val split = this.split("-")
    return split[2]
}

fun String.toMonth(): String {
    val split = this.split("-")
    if(split[1].startsWith("0")){
        return split[1].removePrefix("0")
    }
    return split[1]
}

inline fun <reified T> T.callPrivateFunc(name: String, vararg args: Any?): Any? =
    T::class
        .declaredMemberFunctions
        .firstOrNull { it.name == name }
        ?.apply { isAccessible = true }
        ?.call(this, *args)


@SuppressLint("SimpleDateFormat")
fun String.toCalendar(): Calendar {
    val df: DateFormat = SimpleDateFormat(AppUtils.DATE_FORMAT)
    val cal: Calendar = Calendar.getInstance()
    cal.time = df.parse(this)!!
    return  cal
}

fun Float.toId():Float{
    var result=0f
    when(this){
        100f->result=1f
        150f->result=2f
        200f->result=3f
        250f->result=4f
        300f->result=5f
        500f->result=6f
        600f->result=7f
        700f->result=8f
        800f->result=9f
        900f->result=10f
        1000f->result=11f
        -2f->result=12f
    }
    return result
}

fun Int.toExtractIntookOption(unit: String): String {
    var result = ""
    when(unit){
        "ml"-> result = when(this){
            0-> "50"
            1-> "100"
            2-> "150"
            3-> "200"
            4-> "250"
            5-> "300"
            6-> "500"
            7-> "600"
            8-> "700"
            9-> "800"
            10-> "900"
            11-> "1000"
            12-> "Custom"
            else->"Multi opt."
        }
        "fl oz"-> result = when(this){
            0-> "1.69"
            1-> "3.38"
            2-> "5.07"
            3-> "6.76"
            4-> "8.45"
            5-> "10.14"
            6-> "16.91"
            7-> "20.89"
            8-> "23.67"
            9-> "27.05"
            10-> "30.43"
            11-> "33.81"
            12-> "Custom"
            else->"Multi opt."
        }
    }

    return result;
}
