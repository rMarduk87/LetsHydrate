package rpt.tool.mementobibere.libraries.chart.data;

import rpt.tool.mementobibere.libraries.chart.interfaces.datasets.ICandleDataSet;

import java.util.List;

public class CandleData extends BarLineScatterCandleBubbleData<ICandleDataSet> {

    public CandleData() {
        super();
    }

    public CandleData(List<ICandleDataSet> dataSets) {
        super(dataSets);
    }

    public CandleData(ICandleDataSet... dataSets) {
        super(dataSets);
    }
}
