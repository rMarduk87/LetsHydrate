package rpt.tool.mementobibere.ui.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.ui.chart.components.YAxis.AxisDependency;
import rpt.tool.mementobibere.ui.chart.data.BarLineScatterCandleBubbleData;
import rpt.tool.mementobibere.ui.chart.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
