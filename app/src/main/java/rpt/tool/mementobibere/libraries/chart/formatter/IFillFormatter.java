package rpt.tool.mementobibere.libraries.chart.formatter;

import rpt.tool.mementobibere.libraries.chart.interfaces.dataprovider.LineDataProvider;
import rpt.tool.mementobibere.libraries.chart.interfaces.datasets.ILineDataSet;


public interface IFillFormatter
{

    
    float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider);
}
