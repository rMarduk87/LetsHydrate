package rpt.tool.mementobibere.utils.data.database.models

import rpt.tool.mementobibere.utils.data.DbModel
import java.util.Calendar

class ReachedGoalModel(
    val day: Calendar,
    val quantity: String,
    val unit: String
) : DbModel() {

    init {
    }
}