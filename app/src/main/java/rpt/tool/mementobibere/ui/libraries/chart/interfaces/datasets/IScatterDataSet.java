package rpt.tool.mementobibere.ui.libraries.chart.interfaces.datasets;

import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;
import rpt.tool.mementobibere.ui.libraries.chart.renderer.scatter.IShapeRenderer;
import rpt.tool.mementobibere.ui.libraries.chart.data.Entry;


public interface IScatterDataSet extends ILineScatterCandleRadarDataSet<Entry> {

    
    float getScatterShapeSize();

    
    float getScatterShapeHoleRadius();

    
    int getScatterShapeHoleColor();

    
    IShapeRenderer getShapeRenderer();
}
