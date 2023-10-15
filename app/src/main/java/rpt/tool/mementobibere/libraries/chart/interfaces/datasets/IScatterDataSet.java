package rpt.tool.mementobibere.libraries.chart.interfaces.datasets;

import rpt.tool.mementobibere.libraries.chart.data.Entry;
import rpt.tool.mementobibere.libraries.chart.renderer.scatter.IShapeRenderer;


public interface IScatterDataSet extends ILineScatterCandleRadarDataSet<Entry> {

    
    float getScatterShapeSize();

    
    float getScatterShapeHoleRadius();

    
    int getScatterShapeHoleColor();

    
    IShapeRenderer getShapeRenderer();
}
