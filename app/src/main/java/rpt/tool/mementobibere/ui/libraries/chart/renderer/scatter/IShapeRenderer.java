package rpt.tool.mementobibere.ui.libraries.chart.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IScatterDataSet;
import rpt.tool.mementobibere.ui.libraries.chart.utils.ViewPortHandler;
import rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets.IScatterDataSet;
import rpt.tool.mementobibere.ui.libraries.chart.utils.ViewPortHandler;


public interface IShapeRenderer
{

    
    void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler,
                     float posX, float posY, Paint renderPaint);
}
