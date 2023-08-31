package rpt.tool.mementobibere.ui.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.ui.chart.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
