package rpt.tool.mementobibere.ui.chart.interfaces.datasets;

import rpt.tool.mementobibere.ui.chart.data.BubbleEntry;


public interface IBubbleDataSet extends IBarLineScatterCandleBubbleDataSet<BubbleEntry> {

    
    void setHighlightCircleWidth(float width);

    float getMaxSize();

    boolean isNormalizeSizeEnabled();

    
    float getHighlightCircleWidth();
}
