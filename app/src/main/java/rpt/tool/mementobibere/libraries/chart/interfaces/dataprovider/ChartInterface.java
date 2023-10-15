package rpt.tool.mementobibere.libraries.chart.interfaces.dataprovider;

import android.graphics.RectF;

import rpt.tool.mementobibere.libraries.chart.data.ChartData;
import rpt.tool.mementobibere.libraries.chart.formatter.IValueFormatter;
import rpt.tool.mementobibere.libraries.chart.utils.MPPointF;


public interface ChartInterface {

    
    float getXChartMin();

    
    float getXChartMax();

    float getXRange();

    
    float getYChartMin();

    
    float getYChartMax();

    
    float getMaxHighlightDistance();

    int getWidth();

    int getHeight();

    MPPointF getCenterOfView();

    MPPointF getCenterOffsets();

    RectF getContentRect();

    IValueFormatter getDefaultValueFormatter();

    ChartData getData();

    int getMaxVisibleCount();
}
