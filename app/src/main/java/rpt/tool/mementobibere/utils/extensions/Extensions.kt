package rpt.tool.mementobibere.utils.extensions

import rpt.tool.mementobibere.R


fun String?.toFrequency(): Int {
    when(this){
        "30"-> return 30
        "45"-> return 45
        "60"-> return 60
    }
    return 30
}

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

