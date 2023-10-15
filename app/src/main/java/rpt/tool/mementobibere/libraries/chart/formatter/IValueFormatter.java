package rpt.tool.mementobibere.libraries.chart.formatter;

import rpt.tool.mementobibere.libraries.chart.data.Entry;
import rpt.tool.mementobibere.libraries.chart.utils.ViewPortHandler;


public interface IValueFormatter
{

    
    String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler);
}
