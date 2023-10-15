package rpt.tool.mementobibere.libraries.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.libraries.chart.data.CombinedData;


public interface CombinedDataProvider extends LineDataProvider, BarDataProvider, BubbleDataProvider, CandleDataProvider, ScatterDataProvider {

    CombinedData getCombinedData();
}
