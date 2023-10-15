
package rpt.tool.mementobibere.libraries.chart.formatter;

import rpt.tool.mementobibere.libraries.chart.components.AxisBase;
import rpt.tool.mementobibere.libraries.chart.data.Entry;
import rpt.tool.mementobibere.libraries.chart.utils.ViewPortHandler;

import java.text.DecimalFormat;


public class PercentFormatter implements IValueFormatter, IAxisValueFormatter
{

    protected DecimalFormat mFormat;

    public PercentFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    
    public PercentFormatter(DecimalFormat format) {
        this.mFormat = format;
    }

    // IValueFormatter
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value) + " %";
    }

    // IAxisValueFormatter
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value) + " %";
    }

    public int getDecimalDigits() {
        return 1;
    }
}
