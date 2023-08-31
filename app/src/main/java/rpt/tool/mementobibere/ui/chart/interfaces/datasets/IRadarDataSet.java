package rpt.tool.mementobibere.ui.chart.interfaces.datasets;

import rpt.tool.mementobibere.ui.chart.data.RadarEntry;


public interface IRadarDataSet extends ILineRadarDataSet<RadarEntry> {

    /// flag indicating whether highlight circle should be drawn or not
    boolean isDrawHighlightCircleEnabled();

    /// Sets whether highlight circle should be drawn or not
    void setDrawHighlightCircleEnabled(boolean enabled);

    int getHighlightCircleFillColor();

    /// The stroke color for highlight circle.
    /// If Utils.COLOR_NONE, the color of the dataset is taken.
    int getHighlightCircleStrokeColor();

    int getHighlightCircleStrokeAlpha();

    float getHighlightCircleInnerRadius();

    float getHighlightCircleOuterRadius();

    float getHighlightCircleStrokeWidth();

}
