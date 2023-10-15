package rpt.tool.mementobibere.libraries.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.libraries.chart.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
