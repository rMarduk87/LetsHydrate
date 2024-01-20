package rpt.tool.mementobibere.utils.data.mappers.reachedGoal

import rpt.tool.mementobibere.utils.data.appmodel.ReachedGoal
import rpt.tool.mementobibere.utils.data.database.models.ReachedGoalModel
import rpt.tool.mementobibere.utils.data.mappers.ModelMapper

class ReachedGoalToReachedGoalModel : ModelMapper<ReachedGoal, ReachedGoalModel> {
    override val destination: Class<ReachedGoalModel> = ReachedGoalModel::class.java

    override fun map(source: ReachedGoal): ReachedGoalModel {
        return ReachedGoalModel(
            day = source.day,
            quantity = source.quantity,
            unit = source.unit
        )
    }
}