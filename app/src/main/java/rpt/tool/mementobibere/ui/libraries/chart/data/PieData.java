
package rpt.tool.mementobibere.ui.libraries.chart.data;

import android.util.Log;

import rpt.tool.mementobibere.ui.libraries.chart.highlight.Highlight;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IPieDataSet;

import java.util.List;


public class PieData extends ChartData<IPieDataSet> {

    public PieData() {
        super();
    }

    public PieData(IPieDataSet dataSet) {
        super(dataSet);
    }

    
    public void setDataSet(IPieDataSet dataSet) {
        mDataSets.clear();
        mDataSets.add(dataSet);
        notifyDataChanged();
    }

    
    public IPieDataSet getDataSet() {
        return mDataSets.get(0);
    }

    @Override
    public List<IPieDataSet> getDataSets() {
        List<IPieDataSet> dataSets = super.getDataSets();

        if (dataSets.size() < 1) {
            Log.e("MPAndroidChart",
                    "Found multiple data sets while pie chart only allows one");
        }

        return dataSets;
    }

    
    @Override
    public IPieDataSet getDataSetByIndex(int index) {
        return index == 0 ? getDataSet() : null;
    }

    @Override
    public IPieDataSet getDataSetByLabel(String label, boolean ignorecase) {
        return ignorecase ? label.equalsIgnoreCase(mDataSets.get(0).getLabel()) ? mDataSets.get(0)
                : null : label.equals(mDataSets.get(0).getLabel()) ? mDataSets.get(0) : null;
    }

    @Override
    public Entry getEntryForHighlight(Highlight highlight) {
        return getDataSet().getEntryForIndex((int) highlight.getX());
    }

    
    public float getYValueSum() {

        float sum = 0;

        for (int i = 0; i < getDataSet().getEntryCount(); i++)
            sum += getDataSet().getEntryForIndex(i).getY();


        return sum;
    }
}
