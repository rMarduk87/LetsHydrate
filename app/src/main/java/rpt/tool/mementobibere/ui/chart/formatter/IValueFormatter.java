package rpt.tool.mementobibere.ui.chart.formatter;

import rpt.tool.mementobibere.ui.chart.data.Entry;
import rpt.tool.mementobibere.ui.chart.utils.ViewPortHandler;


public interface IValueFormatter
{

    
    String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler);
}
