package rpt.tool.mementobibere.ui.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.ui.chart.components.YAxis;
import rpt.tool.mementobibere.ui.chart.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
