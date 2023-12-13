package rpt.tool.mementobibere.utils.data.database.models

import rpt.tool.mementobibere.utils.data.DbModel
import java.util.Calendar

class DailyModel (
    val day: Calendar,
    val quantity: Float,
) : DbModel() {

    init {
    }
}