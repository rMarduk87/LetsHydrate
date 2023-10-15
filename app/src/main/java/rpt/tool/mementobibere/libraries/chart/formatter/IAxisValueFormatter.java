package rpt.tool.mementobibere.libraries.chart.formatter;

import rpt.tool.mementobibere.libraries.chart.components.AxisBase;


public interface IAxisValueFormatter
{

    
    String getFormattedValue(float value, AxisBase axis);
}
