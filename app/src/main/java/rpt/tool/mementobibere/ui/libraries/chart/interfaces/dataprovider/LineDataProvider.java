package rpt.tool.mementobibere.ui.libraries.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.ui.libraries.chart.components.YAxis;
import rpt.tool.mementobibere.ui.libraries.chart.data.LineData;
import rpt.tool.mementobibere.ui.libraries.chart.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
