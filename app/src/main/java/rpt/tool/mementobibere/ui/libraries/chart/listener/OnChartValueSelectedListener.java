package rpt.tool.mementobibere.ui.libraries.chart.listener;

import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.highlight.Highlight;
import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.highlight.Highlight;


public interface OnChartValueSelectedListener {

    
    void onValueSelected(Entry e, Highlight h);

    
    void onNothingSelected();
}
