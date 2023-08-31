package rpt.tool.mementobibere.ui.chart.listener;

import rpt.tool.mementobibere.ui.chart.data.DataSet;
import rpt.tool.mementobibere.ui.chart.data.Entry;


public interface OnDrawListener {

	
	void onEntryAdded(Entry entry);

	
	void onEntryMoved(Entry entry);

	
	void onDrawFinished(DataSet<?> dataSet);

}
