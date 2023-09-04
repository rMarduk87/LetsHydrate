package rpt.tool.mementobibere.ui.libraries.chart.formatter;

import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.utils.ViewPortHandler;
import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.utils.ViewPortHandler;


public interface IValueFormatter
{

    
    String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler);
}
