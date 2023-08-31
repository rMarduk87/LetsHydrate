package rpt.tool.mementobibere.ui.chart.listener;

import rpt.tool.mementobibere.ui.chart.data.Entry;
import rpt.tool.mementobibere.ui.chart.highlight.Highlight;


public interface OnChartValueSelectedListener {

    
    void onValueSelected(Entry e, Highlight h);

    
    void onNothingSelected();
}
