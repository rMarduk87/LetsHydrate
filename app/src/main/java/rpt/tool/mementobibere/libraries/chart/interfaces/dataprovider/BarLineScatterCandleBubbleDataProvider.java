package rpt.tool.mementobibere.libraries.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.libraries.chart.components.YAxis;
import rpt.tool.mementobibere.libraries.chart.data.BarLineScatterCandleBubbleData;
import rpt.tool.mementobibere.libraries.chart.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(YAxis.AxisDependency axis);
    boolean isInverted(YAxis.AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
