package rpt.tool.mementobibere.libraries.chart.formatter;

import rpt.tool.mementobibere.libraries.chart.data.Entry;
import rpt.tool.mementobibere.libraries.chart.interfaces.datasets.IDataSet;


public interface ColorFormatter {

    
    int getColor(int index, Entry e, IDataSet set);
}