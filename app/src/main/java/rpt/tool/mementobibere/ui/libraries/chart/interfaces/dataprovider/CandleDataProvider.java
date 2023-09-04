package rpt.tool.mementobibere.ui.libraries.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.ui.libraries.chart.data.CandleData;
import rpt.tool.mementobibere.ui.libraries.chart.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
