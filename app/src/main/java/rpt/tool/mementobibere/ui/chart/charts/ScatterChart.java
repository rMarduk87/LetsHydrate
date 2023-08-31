
package rpt.tool.mementobibere.ui.chart.charts;

import android.content.Context;
import android.util.AttributeSet;

import rpt.tool.mementobibere.ui.chart.data.ScatterData;
import rpt.tool.mementobibere.ui.chart.interfaces.dataprovider.ScatterDataProvider;
import rpt.tool.mementobibere.ui.chart.renderer.ScatterChartRenderer;


public class ScatterChart extends BarLineChartBase<ScatterData> implements ScatterDataProvider {

    public ScatterChart(Context context) {
        super(context);
    }

    public ScatterChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScatterChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void init() {
        super.init();

        mRenderer = new ScatterChartRenderer(this, mAnimator, mViewPortHandler);

        getXAxis().setSpaceMin(0.5f);
        getXAxis().setSpaceMax(0.5f);
    }

    @Override
    public ScatterData getScatterData() {
        return mData;
    }

    
    public enum ScatterShape {

        SQUARE("SQUARE"),
        CIRCLE("CIRCLE"),
        TRIANGLE("TRIANGLE"),
        CROSS("CROSS"),
        X("X"),
        CHEVRON_UP("CHEVRON_UP"),
        CHEVRON_DOWN("CHEVRON_DOWN");

        private final String shapeIdentifier;

        ScatterShape(final String shapeIdentifier) {
            this.shapeIdentifier = shapeIdentifier;
        }

        @Override
        public String toString() {
            return shapeIdentifier;
        }

        public static ScatterShape[] getAllDefaultShapes() {
            return new ScatterShape[]{SQUARE, CIRCLE, TRIANGLE, CROSS, X, CHEVRON_UP, CHEVRON_DOWN};
        }
    }
}
