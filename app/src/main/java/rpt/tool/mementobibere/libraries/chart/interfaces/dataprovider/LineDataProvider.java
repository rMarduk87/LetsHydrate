package rpt.tool.mementobibere.libraries.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.libraries.chart.components.YAxis;
import rpt.tool.mementobibere.libraries.chart.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
