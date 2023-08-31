package rpt.tool.mementobibere.ui.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.ui.chart.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
