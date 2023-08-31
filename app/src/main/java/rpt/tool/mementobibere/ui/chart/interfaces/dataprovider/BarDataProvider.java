package rpt.tool.mementobibere.ui.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.ui.chart.data.BarData;

public interface BarDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BarData getBarData();
    boolean isDrawBarShadowEnabled();
    boolean isDrawValueAboveBarEnabled();
    boolean isHighlightFullBarEnabled();
}
