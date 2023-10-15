package rpt.tool.mementobibere.libraries.chart.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import rpt.tool.mementobibere.libraries.chart.interfaces.datasets.IScatterDataSet;
import rpt.tool.mementobibere.libraries.chart.utils.ViewPortHandler;


public interface IShapeRenderer
{

    
    void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler,
                     float posX, float posY, Paint renderPaint);
}
