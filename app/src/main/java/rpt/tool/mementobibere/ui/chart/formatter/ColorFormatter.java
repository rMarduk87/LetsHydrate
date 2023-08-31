package rpt.tool.mementobibere.ui.chart.formatter;

import rpt.tool.mementobibere.ui.chart.data.Entry;
import rpt.tool.mementobibere.ui.chart.interfaces.datasets.IDataSet;


public interface ColorFormatter {

    
    int getColor(int index, Entry e, IDataSet set);
}