package rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets;

import android.graphics.DashPathEffect;

import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.data.LineDataSet;
import rpt.tool.mementobibere.ui.libraries.chart.formatter.IFillFormatter;
import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.data.LineDataSet;


public interface ILineDataSet extends ILineRadarDataSet<Entry> {

    
    LineDataSet.Mode getMode();

    
    float getCubicIntensity();

    @Deprecated
    boolean isDrawCubicEnabled();

    @Deprecated
    boolean isDrawSteppedEnabled();

    
    float getCircleRadius();

    
    float getCircleHoleRadius();

    
    int getCircleColor(int index);

    
    int getCircleColorCount();

    
    boolean isDrawCirclesEnabled();

    
    int getCircleHoleColor();

    
    boolean isDrawCircleHoleEnabled();

    
    DashPathEffect getDashPathEffect();

    
    boolean isDashedLineEnabled();

    
    IFillFormatter getFillFormatter();
}