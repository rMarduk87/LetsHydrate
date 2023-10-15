package rpt.tool.mementobibere.libraries.chart.listener;

import rpt.tool.mementobibere.libraries.chart.highlight.Highlight;
import rpt.tool.mementobibere.libraries.chart.data.Entry;


public interface OnChartValueSelectedListener {

    
    void onValueSelected(Entry e, Highlight h);

    
    void onNothingSelected();
}
