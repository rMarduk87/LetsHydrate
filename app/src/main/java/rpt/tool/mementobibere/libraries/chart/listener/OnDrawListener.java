package rpt.tool.mementobibere.libraries.chart.listener;

import rpt.tool.mementobibere.libraries.chart.data.DataSet;
import rpt.tool.mementobibere.libraries.chart.data.Entry;


public interface OnDrawListener {

	
	void onEntryAdded(Entry entry);

	
	void onEntryMoved(Entry entry);

	
	void onDrawFinished(DataSet<?> dataSet);

}
