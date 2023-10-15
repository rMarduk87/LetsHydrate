
package rpt.tool.mementobibere.libraries.chart.charts;

import android.content.Context;
import android.util.AttributeSet;

import rpt.tool.mementobibere.libraries.chart.renderer.BubbleChartRenderer;
import rpt.tool.mementobibere.libraries.chart.data.BubbleData;
import rpt.tool.mementobibere.libraries.chart.interfaces.dataprovider.BubbleDataProvider;


public class BubbleChart extends BarLineChartBase<BubbleData> implements BubbleDataProvider {

    public BubbleChart(Context context) {
        super(context);
    }

    public BubbleChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new BubbleChartRenderer(this, mAnimator, mViewPortHandler);
    }

    public BubbleData getBubbleData() {
        return mData;
    }
}
