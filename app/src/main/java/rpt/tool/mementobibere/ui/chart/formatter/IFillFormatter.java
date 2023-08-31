package rpt.tool.mementobibere.ui.chart.formatter;

import rpt.tool.mementobibere.ui.chart.interfaces.dataprovider.LineDataProvider;
import rpt.tool.mementobibere.ui.chart.interfaces.datasets.ILineDataSet;


public interface IFillFormatter
{

    
    float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider);
}
