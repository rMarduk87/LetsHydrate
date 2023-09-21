
package rpt.tool.mementobibere.ui.libraries.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

import rpt.tool.mementobibere.ui.libraries.chart.animation.ChartAnimator;
import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.formatter.IValueFormatter;
import rpt.tool.mementobibere.ui.libraries.chart.highlight.Highlight;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.dataprovider.ChartInterface;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IDataSet;
import rpt.tool.mementobibere.ui.libraries.chart.utils.Utils;
import rpt.tool.mementobibere.ui.libraries.chart.utils.ViewPortHandler;
import rpt.tool.mementobibere.ui.libraries.chart.animation.ChartAnimator;
import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.formatter.IValueFormatter;
import rpt.tool.mementobibere.ui.libraries.chart.highlight.Highlight;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.dataprovider.ChartInterface;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IDataSet;
import rpt.tool.mementobibere.ui.libraries.chart.utils.Utils;
import rpt.tool.mementobibere.ui.libraries.chart.utils.ViewPortHandler;


public abstract class DataRenderer extends Renderer {

    
    protected ChartAnimator mAnimator;

    
    protected Paint mRenderPaint;

    
    protected Paint mHighlightPaint;

    protected Paint mDrawPaint;

    
    protected Paint mValuePaint;

    public DataRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(viewPortHandler);
        this.mAnimator = animator;

        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Style.FILL);

        mDrawPaint = new Paint(Paint.DITHER_FLAG);

        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setColor(Color.rgb(63, 63, 63));
        mValuePaint.setTextAlign(Align.CENTER);
        mValuePaint.setTextSize(Utils.convertDpToPixel(9f));

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));
    }

    protected boolean isDrawingValuesAllowed(ChartInterface chart) {
        return chart.getData().getEntryCount() < chart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX();
    }

    
    public Paint getPaintValues() {
        return mValuePaint;
    }

    
    public Paint getPaintHighlight() {
        return mHighlightPaint;
    }

    
    public Paint getPaintRender() {
        return mRenderPaint;
    }

    
    protected void applyValueTextStyle(IDataSet set) {

        mValuePaint.setTypeface(set.getValueTypeface());
        mValuePaint.setTextSize(set.getValueTextSize());
    }

    
    public abstract void initBuffers();

    
    public abstract void drawData(Canvas c);

    
    public abstract void drawValues(Canvas c);

    
    public void drawValue(Canvas c, IValueFormatter formatter, float value, Entry entry, int dataSetIndex, float x, float y, int color) {
        mValuePaint.setColor(color);
        c.drawText(formatter.getFormattedValue(value, entry, dataSetIndex, mViewPortHandler), x, y, mValuePaint);
    }

    
    public abstract void drawExtras(Canvas c);

    
    public abstract void drawHighlighted(Canvas c, Highlight[] indices);
}
