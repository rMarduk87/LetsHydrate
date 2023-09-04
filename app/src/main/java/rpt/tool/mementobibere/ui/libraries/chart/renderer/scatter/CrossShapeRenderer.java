package rpt.tool.mementobibere.ui.libraries.chart.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IScatterDataSet;
import rpt.tool.mementobibere.ui.libraries.chart.utils.Utils;
import rpt.tool.mementobibere.ui.libraries.chart.utils.ViewPortHandler;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IScatterDataSet;
import rpt.tool.mementobibere.ui.libraries.chart.utils.Utils;
import rpt.tool.mementobibere.ui.libraries.chart.utils.ViewPortHandler;


public class CrossShapeRenderer implements IShapeRenderer
{


    @Override
    public void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler,
                            float posX, float posY, Paint renderPaint) {

        final float shapeHalf = dataSet.getScatterShapeSize() / 2f;

        renderPaint.setStyle(Paint.Style.STROKE);
        renderPaint.setStrokeWidth(Utils.convertDpToPixel(1f));

        c.drawLine(
                posX - shapeHalf,
                posY,
                posX + shapeHalf,
                posY,
                renderPaint);
        c.drawLine(
                posX,
                posY - shapeHalf,
                posX,
                posY + shapeHalf,
                renderPaint);

    }
}
