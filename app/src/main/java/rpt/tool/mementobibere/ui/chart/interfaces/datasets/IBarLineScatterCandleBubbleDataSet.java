package rpt.tool.mementobibere.ui.chart.interfaces.datasets;

import rpt.tool.mementobibere.ui.chart.data.Entry;


public interface IBarLineScatterCandleBubbleDataSet<T extends Entry> extends IDataSet<T> {

    
    int getHighLightColor();
}
