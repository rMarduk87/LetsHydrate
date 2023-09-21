package rpt.tool.mementobibere.utils.chart

import rpt.tool.mementobibere.ui.libraries.chart.components.AxisBase
import rpt.tool.mementobibere.ui.libraries.chart.formatter.IAxisValueFormatter

class ChartXValueFormatter(val dateArray: ArrayList<String>) :
    IAxisValueFormatter {


    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return dateArray.getOrNull(value.toInt()) ?: ""
    }
}