package rpt.tool.mementobibere.ui.libraries.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.ui.libraries.chart.data.BarData;
import rpt.tool.mementobibere.ui.libraries.chart.data.BarData;

public interface BarDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BarData getBarData();
    boolean isDrawBarShadowEnabled();
    boolean isDrawValueAboveBarEnabled();
    boolean isHighlightFullBarEnabled();
}
