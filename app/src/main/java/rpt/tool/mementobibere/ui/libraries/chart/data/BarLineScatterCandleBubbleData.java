
package rpt.tool.mementobibere.ui.libraries.chart.data;

import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;

import java.util.List;


public abstract class BarLineScatterCandleBubbleData<T extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>
        extends ChartData<T> {
    
    public BarLineScatterCandleBubbleData() {
        super();
    }

    public BarLineScatterCandleBubbleData(T... sets) {
        super(sets);
    }

    public BarLineScatterCandleBubbleData(List<T> sets) {
        super(sets);
    }
}
