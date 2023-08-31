package rpt.tool.mementobibere.utils.chart

import rpt.tool.mementobibere.ui.chart.components.AxisBase
import rpt.tool.mementobibere.ui.chart.formatter.IAxisValueFormatter

class ChartXValueFormatter(val dateArray: ArrayList<String>) : IAxisValueFormatter {


    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return dateArray.getOrNull(value.toInt()) ?: ""
    }
}