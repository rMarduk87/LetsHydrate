package rpt.tool.mementobibere.ui.chart.interfaces.datasets;

import android.graphics.drawable.Drawable;

import rpt.tool.mementobibere.ui.chart.data.Entry;


public interface ILineRadarDataSet<T extends Entry> extends ILineScatterCandleRadarDataSet<T> {

    
    int getFillColor();

    
    Drawable getFillDrawable();

    
    int getFillAlpha();

    
    float getLineWidth();

    
    boolean isDrawFilledEnabled();

    
    void setDrawFilled(boolean enabled);
}
