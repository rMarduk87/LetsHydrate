package rpt.tool.mementobibere.ui.chart.formatter;

import rpt.tool.mementobibere.ui.chart.components.AxisBase;


public interface IAxisValueFormatter
{

    
    String getFormattedValue(float value, AxisBase axis);
}
