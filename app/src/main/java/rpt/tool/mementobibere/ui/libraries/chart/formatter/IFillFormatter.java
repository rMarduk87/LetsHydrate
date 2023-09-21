package rpt.tool.mementobibere.ui.libraries.chart.formatter;

import rpt.tool.mementobibere.ui.libraries.chart.interfaces.dataprovider.LineDataProvider;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.ILineDataSet;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.dataprovider.LineDataProvider;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.ILineDataSet;


public interface IFillFormatter
{

    
    float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider);
}
