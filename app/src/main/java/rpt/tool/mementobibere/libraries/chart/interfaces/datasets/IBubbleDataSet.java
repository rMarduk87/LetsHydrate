package rpt.tool.mementobibere.libraries.chart.interfaces.datasets;

import rpt.tool.mementobibere.libraries.chart.data.BubbleEntry;


public interface IBubbleDataSet extends IBarLineScatterCandleBubbleDataSet<BubbleEntry> {

    
    void setHighlightCircleWidth(float width);

    float getMaxSize();

    boolean isNormalizeSizeEnabled();

    
    float getHighlightCircleWidth();
}
