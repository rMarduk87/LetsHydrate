package rpt.tool.mementobibere.libraries.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.libraries.chart.data.BarData;

public interface BarDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BarData getBarData();
    boolean isDrawBarShadowEnabled();
    boolean isDrawValueAboveBarEnabled();
    boolean isHighlightFullBarEnabled();
}
