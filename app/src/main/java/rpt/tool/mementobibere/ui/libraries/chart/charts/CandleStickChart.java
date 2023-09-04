
package rpt.tool.mementobibere.ui.libraries.chart.charts;

import android.content.Context;
import android.util.AttributeSet;

import rpt.tool.mementobibere.ui.libraries.chart.data.CandleData;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.dataprovider.CandleDataProvider;
import rpt.tool.mementobibere.ui.libraries.chart.renderer.CandleStickChartRenderer;


public class CandleStickChart extends BarLineChartBase<CandleData> implements CandleDataProvider {

    public CandleStickChart(Context context) {
        super(context);
    }

    public CandleStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CandleStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new CandleStickChartRenderer(this, mAnimator, mViewPortHandler);

        getXAxis().setSpaceMin(0.5f);
        getXAxis().setSpaceMax(0.5f);
    }

    @Override
    public CandleData getCandleData() {
        return mData;
    }
}
