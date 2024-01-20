package rpt.tool.mementobibere.utils.data.appmodel

import androidx.annotation.Keep
import rpt.tool.mementobibere.utils.data.AppModel
import rpt.tool.mementobibere.utils.data.mappers.addMapper
import rpt.tool.mementobibere.utils.data.mappers.reachedGoal.ReachedGoalToReachedGoalModel
import java.io.Serializable
import java.util.Calendar

@Keep
class ReachedGoal(
    var day: Calendar,
    val quantity: String,
    val unit: String
) : AppModel(), Serializable {

    init {
        addMapper(ReachedGoalToReachedGoalModel())
    }
}