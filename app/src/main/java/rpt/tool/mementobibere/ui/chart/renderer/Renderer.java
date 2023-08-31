
package rpt.tool.mementobibere.ui.chart.renderer;

import rpt.tool.mementobibere.ui.chart.utils.ViewPortHandler;


public abstract class Renderer {

    
    protected ViewPortHandler mViewPortHandler;

    public Renderer(ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }
}
