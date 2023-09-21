package rpt.tool.mementobibere.ui.libraries.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.ui.libraries.chart.data.CombinedData;
import rpt.tool.mementobibere.ui.libraries.chart.data.CombinedData;


public interface CombinedDataProvider extends LineDataProvider, BarDataProvider, BubbleDataProvider, CandleDataProvider, ScatterDataProvider {

    CombinedData getCombinedData();
}
