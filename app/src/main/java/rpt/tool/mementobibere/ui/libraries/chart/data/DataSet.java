
package rpt.tool.mementobibere.ui.libraries.chart.data;

import java.util.ArrayList;
import java.util.List;


public abstract class DataSet<T extends Entry> extends BaseDataSet<T> {

    
    protected List<T> mEntries;

    
    protected float mYMax = -Float.MAX_VALUE;

    
    protected float mYMin = Float.MAX_VALUE;

    
    protected float mXMax = -Float.MAX_VALUE;

    
    protected float mXMin = Float.MAX_VALUE;


    
    public DataSet(List<T> entries, String label) {
        super(label);
        this.mEntries = entries;

        if (mEntries == null)
            mEntries = new ArrayList<T>();

        calcMinMax();
    }

    @Override
    public void calcMinMax() {

        mYMax = -Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;
        mXMax = -Float.MAX_VALUE;
        mXMin = Float.MAX_VALUE;

        if (mEntries == null || mEntries.isEmpty())
            return;

        for (T e : mEntries) {
            calcMinMax(e);
        }
    }

    @Override
    public void calcMinMaxY(float fromX, float toX) {
        mYMax = -Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;
        
        if (mEntries == null || mEntries.isEmpty())
            return;

        int indexFrom = getEntryIndex(fromX, Float.NaN, Rounding.DOWN);
        int indexTo = getEntryIndex(toX, Float.NaN, Rounding.UP);

        if (indexTo < indexFrom) return;

        for (int i = indexFrom; i <= indexTo; i++) {

            // only recalculate y
            calcMinMaxY(mEntries.get(i));
        }
    }

    
    protected void calcMinMax(T e) {

        if (e == null)
            return;

        calcMinMaxX(e);

        calcMinMaxY(e);
    }

    protected void calcMinMaxX(T e) {

        if (e.getX() < mXMin)
            mXMin = e.getX();

        if (e.getX() > mXMax)
            mXMax = e.getX();
    }

    protected void calcMinMaxY(T e) {

        if (e.getY() < mYMin)
            mYMin = e.getY();

        if (e.getY() > mYMax)
            mYMax = e.getY();
    }

    @Override
    public int getEntryCount() {
        return mEntries.size();
    }

    
    @Deprecated
    public List<T> getValues() {
        return mEntries;
    }

    
    public List<T> getEntries() {
        return mEntries;
    }

    
    @Deprecated
    public void setValues(List<T> values) {
        setEntries(values);
    }

    
    public void setEntries(List<T> entries) {
        mEntries = entries;
        notifyDataSetChanged();
    }

    
    public abstract DataSet<T> copy();

    
    protected void copy(DataSet dataSet) {
        super.copy(dataSet);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(toSimpleString());
        for (int i = 0; i < mEntries.size(); i++) {
            buffer.append(mEntries.get(i).toString() + " ");
        }
        return buffer.toString();
    }

    
    public String toSimpleString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DataSet, label: " + (getLabel() == null ? "" : getLabel()) + ", entries: " + mEntries.size() +
                "\n");
        return buffer.toString();
    }

    @Override
    public float getYMin() {
        return mYMin;
    }

    @Override
    public float getYMax() {
        return mYMax;
    }

    @Override
    public float getXMin() {
        return mXMin;
    }

    @Override
    public float getXMax() {
        return mXMax;
    }

    @Override
    public void addEntryOrdered(T e) {

        if (e == null)
            return;

        if (mEntries == null) {
            mEntries = new ArrayList<T>();
        }

        calcMinMax(e);

        if (mEntries.size() > 0 && mEntries.get(mEntries.size() - 1).getX() > e.getX()) {
            int closestIndex = getEntryIndex(e.getX(), e.getY(), Rounding.UP);
            mEntries.add(closestIndex, e);
        } else {
            mEntries.add(e);
        }
    }

    @Override
    public void clear() {
        mEntries.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean addEntry(T e) {

        if (e == null)
            return false;

        List<T> values = getEntries();
        if (values == null) {
            values = new ArrayList<>();
        }

        calcMinMax(e);

        // add the entry
        return values.add(e);
    }

    @Override
    public boolean removeEntry(T e) {

        if (e == null)
            return false;

        if (mEntries == null)
            return false;

        // remove the entry
        boolean removed = mEntries.remove(e);

        if (removed) {
            calcMinMax();
        }

        return removed;
    }

    @Override
    public int getEntryIndex(Entry e) {
        return mEntries.indexOf(e);
    }

    @Override
    public T getEntryForXValue(float xValue, float closestToY, Rounding rounding) {

        int index = getEntryIndex(xValue, closestToY, rounding);
        if (index > -1)
            return mEntries.get(index);
        return null;
    }

    @Override
    public T getEntryForXValue(float xValue, float closestToY) {
        return getEntryForXValue(xValue, closestToY, Rounding.CLOSEST);
    }

    @Override
    public T getEntryForIndex(int index) {
        return mEntries.get(index);
    }

    @Override
    public int getEntryIndex(float xValue, float closestToY, Rounding rounding) {

        if (mEntries == null || mEntries.isEmpty())
            return -1;

        int low = 0;
        int high = mEntries.size() - 1;
        int closest = high;

        while (low < high) {
            int m = (low + high) / 2;

            final float d1 = mEntries.get(m).getX() - xValue,
                    d2 = mEntries.get(m + 1).getX() - xValue,
                    ad1 = Math.abs(d1), ad2 = Math.abs(d2);

            if (ad2 < ad1) {
                // [m + 1] is closer to xValue
                // Search in an higher place
                low = m + 1;
            } else if (ad1 < ad2) {
                // [m] is closer to xValue
                // Search in a lower place
                high = m;
            } else {
                // We have multiple sequential x-value with same distance

                if (d1 >= 0.0) {
                    // Search in a lower place
                    high = m;
                } else if (d1 < 0.0) {
                    // Search in an higher place
                    low = m + 1;
                }
            }

            closest = high;
        }

        if (closest != -1) {
            float closestXValue = mEntries.get(closest).getX();
            if (rounding == Rounding.UP) {
                // If rounding up, and found x-value is lower than specified x, and we can go upper...
                if (closestXValue < xValue && closest < mEntries.size() - 1) {
                    ++closest;
                }
            } else if (rounding == Rounding.DOWN) {
                // If rounding down, and found x-value is upper than specified x, and we can go lower...
                if (closestXValue > xValue && closest > 0) {
                    --closest;
                }
            }

            // Search by closest to y-value
            if (!Float.isNaN(closestToY)) {
                while (closest > 0 && mEntries.get(closest - 1).getX() == closestXValue)
                    closest -= 1;

                float closestYValue = mEntries.get(closest).getY();
                int closestYIndex = closest;

                while (true) {
                    closest += 1;
                    if (closest >= mEntries.size())
                        break;

                    final Entry value = mEntries.get(closest);

                    if (value.getX() != closestXValue)
                        break;

                    if (Math.abs(value.getY() - closestToY) <= Math.abs(closestYValue - closestToY)) {
                        closestYValue = closestToY;
                        closestYIndex = closest;
                    }
                }

                closest = closestYIndex;
            }
        }

        return closest;
    }

    @Override
    public List<T> getEntriesForXValue(float xValue) {

        List<T> entries = new ArrayList<T>();

        int low = 0;
        int high = mEntries.size() - 1;

        while (low <= high) {
            int m = (high + low) / 2;
            T entry = mEntries.get(m);

            // if we have a match
            if (xValue == entry.getX()) {
                while (m > 0 && mEntries.get(m - 1).getX() == xValue)
                    m--;

                high = mEntries.size();

                // loop over all "equal" entries
                for (; m < high; m++) {
                    entry = mEntries.get(m);
                    if (entry.getX() == xValue) {
                        entries.add(entry);
                    } else {
                        break;
                    }
                }

                break;
            } else {
                if (xValue > entry.getX())
                    low = m + 1;
                else
                    high = m - 1;
            }
        }

        return entries;
    }

    
    public enum Rounding {
        UP,
        DOWN,
        CLOSEST,
    }
}