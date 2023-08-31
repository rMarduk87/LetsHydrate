package rpt.tool.mementobibere.ui.chart.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import rpt.tool.mementobibere.ui.chart.interfaces.datasets.IScatterDataSet;
import rpt.tool.mementobibere.ui.chart.utils.ViewPortHandler;


public interface IShapeRenderer
{

    
    void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler,
                     float posX, float posY, Paint renderPaint);
}
