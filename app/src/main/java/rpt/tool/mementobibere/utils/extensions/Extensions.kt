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
    }
    return R.style.MainTheme
}

fun Int.toAppTheme(): Int {
    when(this){
        0->return R.style.AppTheme
    }
    return R.style.AppTheme
}
