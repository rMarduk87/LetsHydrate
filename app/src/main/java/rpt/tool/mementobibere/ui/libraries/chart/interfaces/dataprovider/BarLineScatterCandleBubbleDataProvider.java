package rpt.tool.mementobibere.ui.libraries.chart.interfaces.dataprovider;

import rpt.tool.mementobibere.ui.libraries.chart.components.YAxis.AxisDependency;
import rpt.tool.mementobibere.ui.libraries.chart.data.BarLineScatterCandleBubbleData;
import rpt.tool.mementobibere.ui.libraries.chart.utils.Transformer;
import rpt.tool.mementobibere.ui.libraries.chart.data.BarLineScatterCandleBubbleData;
import rpt.tool.mementobibere.ui.libraries.chart.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
