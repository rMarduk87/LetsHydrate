package rpt.tool.mementobibere.libraries.chart.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import rpt.tool.mementobibere.libraries.chart.interfaces.datasets.IScatterDataSet;
import rpt.tool.mementobibere.libraries.chart.utils.Utils;
import rpt.tool.mementobibere.libraries.chart.utils.ViewPortHandler;


public class ChevronUpShapeRenderer implements IShapeRenderer
{


    @Override
    public void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler,
                            float posX, float posY, Paint renderPaint) {

        final float shapeHalf = dataSet.getScatterShapeSize() / 2f;

        renderPaint.setStyle(Paint.Style.STROKE);
        renderPaint.setStrokeWidth(Utils.convertDpToPixel(1f));

        c.drawLine(
                posX,
                posY - (2 * shapeHalf),
                posX + (2 * shapeHalf),
                posY,
                renderPaint);

        c.drawLine(
                posX,
                posY - (2 * shapeHalf),
                posX - (2 * shapeHalf),
                posY,
                renderPaint);

    }
}
