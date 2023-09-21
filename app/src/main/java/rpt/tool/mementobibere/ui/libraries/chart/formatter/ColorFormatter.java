package rpt.tool.mementobibere.ui.libraries.chart.formatter;

import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IDataSet;
import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IDataSet;


public interface ColorFormatter {

    
    int getColor(int index, Entry e, IDataSet set);
}