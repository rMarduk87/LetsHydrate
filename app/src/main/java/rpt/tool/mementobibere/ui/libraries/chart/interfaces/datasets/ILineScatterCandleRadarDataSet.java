package rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets;

import android.graphics.DashPathEffect;

import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;


public interface ILineScatterCandleRadarDataSet<T extends Entry> extends IBarLineScatterCandleBubbleDataSet<T> {

    
    boolean isVerticalHighlightIndicatorEnabled();

    
    boolean isHorizontalHighlightIndicatorEnabled();

    
    float getHighlightLineWidth();

    
    DashPathEffect getDashPathEffectHighlight();
}
