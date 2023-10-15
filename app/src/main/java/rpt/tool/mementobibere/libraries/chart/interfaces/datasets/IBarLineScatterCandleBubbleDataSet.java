package rpt.tool.mementobibere.libraries.chart.interfaces.datasets;

import rpt.tool.mementobibere.libraries.chart.data.Entry;


public interface IBarLineScatterCandleBubbleDataSet<T extends Entry> extends IDataSet<T> {

    
    int getHighLightColor();
}
