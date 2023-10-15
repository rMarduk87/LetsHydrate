package rpt.tool.mementobibere.libraries.chart.components;

import android.graphics.Canvas;

import rpt.tool.mementobibere.libraries.chart.data.Entry;
import rpt.tool.mementobibere.libraries.chart.highlight.Highlight;
import rpt.tool.mementobibere.libraries.chart.utils.MPPointF;

public interface IMarker {

    
    MPPointF getOffset();

    
    MPPointF getOffsetForDrawingAtPoint(float posX, float posY);

    
    void refreshContent(Entry e, Highlight highlight);

    
    void draw(Canvas canvas, float posX, float posY);
}
