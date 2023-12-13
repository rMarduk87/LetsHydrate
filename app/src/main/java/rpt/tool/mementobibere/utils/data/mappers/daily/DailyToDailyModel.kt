package rpt.tool.mementobibere.utils.data.mappers.daily

import rpt.tool.mementobibere.utils.data.appmodel.Daily
import rpt.tool.mementobibere.utils.data.database.models.DailyModel
import rpt.tool.mementobibere.utils.data.mappers.ModelMapper

class DailyToDailyModel : ModelMapper<Daily, DailyModel> {
    override val destination: Class<DailyModel> = DailyModel::class.java

    override fun map(source: Daily): DailyModel {
        return DailyModel(
            day = source.day,
            quantity = source.quantity
        )
    }
}