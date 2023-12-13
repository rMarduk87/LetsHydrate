package rpt.tool.mementobibere.utils.data.appmodel

import androidx.annotation.Keep
import rpt.tool.mementobibere.utils.data.AppModel
import rpt.tool.mementobibere.utils.data.mappers.addMapper
import rpt.tool.mementobibere.utils.data.mappers.daily.DailyToDailyModel
import java.io.Serializable
import java.util.Calendar

@Keep
class Daily(
    var day: Calendar,
    val quantity: Float,
) : AppModel(), Serializable {

    init {
        addMapper(DailyToDailyModel())
    }
}