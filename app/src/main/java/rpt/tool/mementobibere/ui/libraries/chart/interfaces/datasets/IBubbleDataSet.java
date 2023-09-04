package rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets;

import rpt.tool.mementobibere.ui.libraries.chart.data.BubbleEntry;
import rpt.tool.mementobibere.ui.libraries.chart.data.BubbleEntry;


public interface IBubbleDataSet extends IBarLineScatterCandleBubbleDataSet<BubbleEntry> {

    
    void setHighlightCircleWidth(float width);

    float getMaxSize();

    boolean isNormalizeSizeEnabled();

    
    float getHighlightCircleWidth();
}
