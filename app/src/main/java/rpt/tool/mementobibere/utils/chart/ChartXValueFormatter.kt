package rpt.tool.mementobibere.utils.chart

import rpt.tool.mementobibere.libraries.chart.components.AxisBase
import rpt.tool.mementobibere.libraries.chart.formatter.IAxisValueFormatter

class ChartXValueFormatter(private val dateArray: ArrayList<String>) :
    IAxisValueFormatter {


    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return dateArray.getOrNull(value.toInt()) ?: ""
    }
}