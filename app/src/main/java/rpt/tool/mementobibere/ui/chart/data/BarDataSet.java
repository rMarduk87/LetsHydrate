
package rpt.tool.mementobibere.ui.chart.data;

import android.graphics.Color;

import rpt.tool.mementobibere.ui.chart.interfaces.datasets.IBarDataSet;
import rpt.tool.mementobibere.ui.chart.utils.Fill;

import java.util.ArrayList;
import java.util.List;

public class BarDataSet extends BarLineScatterCandleBubbleDataSet<BarEntry> implements IBarDataSet {

    
    private int mStackSize = 1;

    
    private int mBarShadowColor = Color.rgb(215, 215, 215);

    private float mBarBorderWidth = 0.0f;

    private int mBarBorderColor = Color.BLACK;

    
    private int mHighLightAlpha = 120;

    
    private int mEntryCountStacks = 0;

    
    private String[] mStackLabels = new String[]{};

    protected List<Fill> mFills = null;

    public BarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);

        mHighLightColor = Color.rgb(0, 0, 0);

        calcStackSize(yVals);
        calcEntryCountIncludingStacks(yVals);
    }

    @Override
    public DataSet<BarEntry> copy() {
        List<BarEntry> entries = new ArrayList<BarEntry>();
        for (int i = 0; i < mEntries.size(); i++) {
            entries.add(mEntries.get(i).copy());
        }
        BarDataSet copied = new BarDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(BarDataSet barDataSet) {
        super.copy(barDataSet);
        barDataSet.mStackSize = mStackSize;
        barDataSet.mBarShadowColor = mBarShadowColor;
        barDataSet.mBarBorderWidth = mBarBorderWidth;
        barDataSet.mStackLabels = mStackLabels;
        barDataSet.mHighLightAlpha = mHighLightAlpha;
    }

    @Override
    public List<Fill> getFills() {
        return mFills;
    }

    @Override
    public Fill getFill(int index) {
        return mFills.get(index % mFills.size());
    }

    
    @Deprecated
    public List<Fill> getGradients() {
        return mFills;
    }

    
    @Deprecated
    public Fill getGradient(int index) {
        return getFill(index);
    }

    
    public void setGradientColor(int startColor, int endColor) {
        mFills.clear();
        mFills.add(new Fill(startColor, endColor));
    }

    
    @Deprecated
    public void setGradientColors(List<Fill> gradientColors) {
        this.mFills = gradientColors;
    }

    
    public void setFills(List<Fill> fills) {
        this.mFills = fills;
    }

    
    private void calcEntryCountIncludingStacks(List<BarEntry> yVals) {

        mEntryCountStacks = 0;

        for (int i = 0; i < yVals.size(); i++) {

            float[] vals = yVals.get(i).getYVals();

            if (vals == null)
                mEntryCountStacks++;
            else
                mEntryCountStacks += vals.length;
        }
    }

    
    private void calcStackSize(List<BarEntry> yVals) {

        for (int i = 0; i < yVals.size(); i++) {

            float[] vals = yVals.get(i).getYVals();

            if (vals != null && vals.length > mStackSize)
                mStackSize = vals.length;
        }
    }

    @Override
    protected void calcMinMax(BarEntry e) {

        if (e != null && !Float.isNaN(e.getY())) {

            if (e.getYVals() == null) {

                if (e.getY() < mYMin)
                    mYMin = e.getY();

                if (e.getY() > mYMax)
                    mYMax = e.getY();
            } else {

                if (-e.getNegativeSum() < mYMin)
                    mYMin = -e.getNegativeSum();

                if (e.getPositiveSum() > mYMax)
                    mYMax = e.getPositiveSum();
            }

            calcMinMaxX(e);
        }
    }

    @Override
    public int getStackSize() {
        return mStackSize;
    }

    @Override
    public boolean isStacked() {
        return mStackSize > 1 ? true : false;
    }

    
    public int getEntryCountStacks() {
        return mEntryCountStacks;
    }

    
    public void setBarShadowColor(int color) {
        mBarShadowColor = color;
    }

    @Override
    public int getBarShadowColor() {
        return mBarShadowColor;
    }

    
    public void setBarBorderWidth(float width) {
        mBarBorderWidth = width;
    }

    
    @Override
    public float getBarBorderWidth() {
        return mBarBorderWidth;
    }

    
    public void setBarBorderColor(int color) {
        mBarBorderColor = color;
    }

    
    @Override
    public int getBarBorderColor() {
        return mBarBorderColor;
    }

    
    public void setHighLightAlpha(int alpha) {
        mHighLightAlpha = alpha;
    }

    @Override
    public int getHighLightAlpha() {
        return mHighLightAlpha;
    }

    
    public void setStackLabels(String[] labels) {
        mStackLabels = labels;
    }

    @Override
    public String[] getStackLabels() {
        return mStackLabels;
    }
}
