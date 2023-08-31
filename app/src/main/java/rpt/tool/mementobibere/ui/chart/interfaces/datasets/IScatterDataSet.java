package rpt.tool.mementobibere.ui.chart.interfaces.datasets;

import rpt.tool.mementobibere.ui.chart.data.Entry;
import rpt.tool.mementobibere.ui.chart.renderer.scatter.IShapeRenderer;


public interface IScatterDataSet extends ILineScatterCandleRadarDataSet<Entry> {

    
    float getScatterShapeSize();

    
    float getScatterShapeHoleRadius();

    
    int getScatterShapeHoleColor();

    
    IShapeRenderer getShapeRenderer();
}
